/**
 * @file ec.cpp
 * @author Alexandre TULLOT (Alexandre.TULLOT@student.isae-supaero.fr)
 * @brief  Implements the classes from ec.hpp
 * @version 0.1
 * @date 2022-07-05
 *
 * File initially copied from https://github.com/emp-toolkit/emp-tool/blob/master/emp-tool/utils/group_openssl.h
 */

#include "ec.hpp"
#include <iostream>
#include <openssl/sha.h>

void error(const char *s, int line = 0, const char *file = nullptr)
{
    fprintf(stderr, s, "\n");
    if (file != nullptr)
    {
        fprintf(stderr, "at %d, %s\n", line, file);
    }
    exit(1);
}

BigInt::BigInt()
{
    n = BN_new();
}

BigInt::BigInt(const BigInt &oth)
{
    n = BN_new();
    BN_copy(n, oth.n);
}

BigInt &BigInt::operator=(BigInt oth)
{
    std::swap(n, oth.n);
    return *this;
}

BigInt::~BigInt()
{
    if (n != nullptr)
        BN_free(n);
}

int BigInt::size()
{
    return BN_num_bytes(n);
}

void BigInt::to_bin(unsigned char *in)
{
    BN_bn2bin(n, in);
}

BigInt BigInt::toHash()
{
    unsigned char bin[BN_num_bytes(n)];
    BN_bn2bin(n, bin);

    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256_CTX sha256;
    SHA256_Init(&sha256);
    SHA224_Update(&sha256, bin, sizeof(bin));
    SHA256_Final(hash, &sha256);

    BigInt ret;
    BN_bin2bn(hash, sizeof(hash), ret.n);
    return ret;
}


void BigInt::from_bin(const unsigned char *in, int length)
{
    BN_free(n);
    n = BN_bin2bn(in, length, nullptr);
}

void BigInt::fromInt(uint32_t x) 
{
    // FIXME memory leak
    std::string dec = std::to_string(x);
    BN_dec2bn(&n, dec.c_str());
}

BigInt BigInt::add(const BigInt &oth)
{
    BigInt ret;
    BN_add(ret.n, n, oth.n);
    return ret;
}

BigInt BigInt::mul_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx)
{
    BigInt ret;
    BN_mod_mul(ret.n, n, b.n, m.n, ctx);
    return ret;
}

BigInt BigInt::exp_mod(const BigInt &b, Group ECGroup)
{
    BigInt m;
    if (EC_GROUP_get_curve(ECGroup.ec_group, m.n, nullptr, nullptr, nullptr) == 0)
    {
        std::cout << "Error getting the curve parameters" << std::endl;
        exit(1);
    }

    return mul_mod(b, m, ECGroup.bn_ctx);
}

BigInt BigInt::exp_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx)
{
    BigInt ret;
    BN_mod_exp(ret.n, n, b.n, m.n, ctx);
    return ret;
}

BigInt BigInt::inv_mod(const BigInt &m, BN_CTX *ctx) const
{
    BigInt ret;
    BN_mod_inverse(ret.n, n, m.n, ctx);
    return ret;
}

BigInt BigInt::add_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx)
{
    BigInt ret;
    BN_mod_add(ret.n, n, b.n, m.n, ctx);
    return ret;
}

BigInt BigInt::mul(const BigInt &oth, BN_CTX *ctx)
{
    BigInt ret;
    BN_mul(ret.n, n, oth.n, ctx);
    return ret;
}

BigInt BigInt::mod(const BigInt &oth, BN_CTX *ctx)
{
    BigInt ret;
    BN_mod(ret.n, n, oth.n, ctx);
    return ret;
}

bool BigInt::operator==(BigInt &oth)
{
    int ret = BN_cmp(n, oth.n);
    return (ret == 0);
}

Point::Point(Group *ECGroup)
{
    if (ECGroup == nullptr)
        return;
    this->group = ECGroup;
    point = EC_POINT_new(group->ec_group);
}

Point::~Point()
{
    if (point != nullptr)
        EC_POINT_free(point);
}

Point::Point(const Point &P)
{
    if (P.group == nullptr)
        return;
    this->group = P.group;
    point = EC_POINT_new(group->ec_group);
    int ret = EC_POINT_copy(point, P.point);
    if (ret == 0)
        error("ECC COPY");
}

Point &Point::operator=(Point P)
{
    std::swap(P.point, point);
    std::swap(P.group, group);
    return *this;
}

void Point::print()
{
    BigInt x, y;
    EC_POINT_get_affine_coordinates(group->ec_group, point, x.n, y.n, group->bn_ctx);
    std::cout << "x = 0x";
    BN_print_fp(stdout, x.n);
    std::cout << std::endl
              << "y = 0x";
    BN_print_fp(stdout, y.n);
    std::cout << std::endl;
}

void Point::to_bin(unsigned char *buf, size_t buf_len)
{
    int ret = EC_POINT_point2oct(group->ec_group, point, POINT_CONVERSION_UNCOMPRESSED, buf, buf_len, group->bn_ctx);
    if (ret == 0)
        error("ECC TO_BIN");
}

size_t Point::size()
{
    size_t ret = EC_POINT_point2oct(group->ec_group, point, POINT_CONVERSION_UNCOMPRESSED, NULL, 0, group->bn_ctx);
    if (ret == 0)
        error("ECC SIZE_BIN");
    return ret;
}

void Point::from_bin(Group *ECGroup, const unsigned char *buf, size_t buf_len)
{
    if (point == nullptr)
    {
        group = ECGroup;
        point = EC_POINT_new(group->ec_group);
    }
    int ret = EC_POINT_oct2point(group->ec_group, point, buf, buf_len, group->bn_ctx);
    if (ret == 0)
        error("ECC FROM_BIN");
}

void Point::fromHash(Group *ECGroup, BigInt x)
{
    if (point == nullptr)
    {
        group = ECGroup;
        point = EC_POINT_new(group->ec_group);
    }

    BigInt a;
    BigInt b;
    BigInt m;
    BN_CTX *ctx = group->bn_ctx;

    if (EC_GROUP_get_curve(ECGroup->ec_group, m.n, a.n, b.n, nullptr) == 0)
    {
        std::cout << "Error at get curve" << std::endl;
        exit(1);
    }

    BigInt incr;
    BN_one(incr.n);

    // generate y
    // y^2 = x^3 + ax + b
    BigInt y;
    BigInt y2(x);
    y2 = y2.mul_mod(x, m, ctx).mul_mod(x, m, ctx);
    a = a.mul_mod(x, m, ctx);
    y2 = y2.add_mod(a, m, ctx).add_mod(b, m, ctx);
    if (BN_mod_sqrt(y.n, y2.n, m.n, ctx))
    {
        int flag = EC_POINT_set_affine_coordinates(group->ec_group, point, x.n, y.n, ctx);
        if (flag == 0)
        {
            std::cout << "Error: set affine coord" << std::endl;
            exit(1);
        }
        if (!is_on_curve())
        {
            std::cout << "Error: HashToCurve didn't generate a point on the curve" << std::endl;
            exit(1);
        }
        return;
    }
    x = x.add(incr);
    fromHash(ECGroup, x);
}

BigInt Point::toHash()
{
    BigInt x, y;
    EC_POINT_get_affine_coordinates(group->ec_group, point, x.n, y.n, group->bn_ctx);
    unsigned char xbin[BN_num_bytes(x.n)];
    unsigned char ybin[BN_num_bytes(y.n)];
    BN_bn2bin(x.n, xbin);
    BN_bn2bin(y.n, ybin);

    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256_CTX sha256;
    SHA256_Init(&sha256);
    SHA224_Update(&sha256, xbin, sizeof(xbin));
    SHA224_Update(&sha256, ybin, sizeof(ybin));
    SHA256_Final(hash, &sha256);

    BigInt ret;
    BN_bin2bn(hash, sizeof(hash), ret.n);
    return ret;
}

bool Point::is_on_curve()
{
    return EC_POINT_is_on_curve(group->ec_group, point, group->bn_ctx) > 0;
}

bool Point::is_empty()
{
    return point == nullptr;
}

Point Point::add(Point &rhs)
{
    Point Ret(group);
    int res = EC_POINT_add(group->ec_group, Ret.point, point, rhs.point, group->bn_ctx);
    if (res == 0)
        error("ECC ADD");
    return Ret;
}

Point Point::mul(const BigInt &m)
{
    Point Ret(group);
    int res = EC_POINT_mul(group->ec_group, Ret.point, NULL, point, m.n, group->bn_ctx);
    if (res == 0)
        error("ECC MUL");
    return Ret;
}

Point Point::inv()
{
    Point Ret(*this);
    int res = EC_POINT_invert(group->ec_group, Ret.point, group->bn_ctx);
    if (res == 0)
        error("ECC INV");
    return Ret;
}

bool Point::operator==(Point &rhs)
{
    int ret = EC_POINT_cmp(group->ec_group, point, rhs.point, group->bn_ctx);
    if (ret == -1)
        error("ECC CMP");
    return (ret == 0);
}

bool Point::operator!=(Point &rhs)
{
    return !(this->operator==(rhs));
}

Group::Group()
{
    ec_group = EC_GROUP_new_by_curve_name(NID_X9_62_prime256v1); // NIST P-256
    bn_ctx = BN_CTX_new();
    EC_GROUP_get_order(ec_group, order.n, bn_ctx);
    scratch = new unsigned char[scratch_size];
}

Group::~Group()
{
    // FIXME MEMORY LEAK...
    // if (ec_group != nullptr)
    //     EC_GROUP_free(ec_group);

    // if (bn_ctx != nullptr)
    //     BN_CTX_free(bn_ctx);

    // if (scratch != nullptr)
    //     delete[] scratch;
}

void Group::resize_scratch(size_t size)
{
    if (size > scratch_size)
    {
        delete[] scratch;
        scratch_size = size;
        scratch = new unsigned char[scratch_size];
    }
}

void Group::get_rand_bn(BigInt &n)
{
    BN_rand_range(n.n, order.n);
}

void Group::get_rand_point(Point &p)
{
    BigInt r;
    get_rand_bn(r);
    p.fromHash(this, r);
}

Point Group::get_generator()
{
    Point Res(this);
    int ret = EC_POINT_copy(Res.point, EC_GROUP_get0_generator(ec_group));
    if (ret == 0)
        error("ECC GEN");
    return Res;
}

Point Group::mul_gen(const BigInt &m)
{
    Point Res(this);
    int ret = EC_POINT_mul(ec_group, Res.point, m.n, NULL, NULL, bn_ctx);
    if (ret == 0)
        error("ECC GEN MUL");
    return Res;
}