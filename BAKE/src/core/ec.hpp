/**
 * @file ec.hpp
 * @author Alexandre TULLOT (Alexandre.TULLOT@student.isae-supaero.fr)
 * @brief  Provides useful extensions of openssl classes related to elliptic curves
 * @version 0.1
 * @date 2022-07-05
 *
 * File initially copied from https://github.com/emp-toolkit/emp-tool/blob/master/emp-tool/utils/group.h
 */

#pragma once

#include <openssl/ec.h>
#include <openssl/bn.h>
#include <openssl/obj_mac.h>

class Group;

/**
 * @brief Represents big integers
 * Based on the openssl BIGNUM
 */
class BigInt
{
public:
    BIGNUM *n = nullptr;
    BigInt();
    BigInt(const BigInt &oth);
    BigInt &operator=(BigInt oth);
    ~BigInt();

    int size();
    void to_bin(unsigned char *in);
    BigInt toHash();
    void from_bin(const unsigned char *in, int length);
    void fromInt(uint32_t x);

    BigInt add(const BigInt &oth);
    BigInt mul(const BigInt &oth, BN_CTX *ctx);
    BigInt mod(const BigInt &oth, BN_CTX *ctx);
    BigInt add_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx);
    BigInt mul_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx);
    BigInt exp_mod(const BigInt &b, const BigInt &m, BN_CTX *ctx);
    BigInt exp_mod(const BigInt &b, Group ECGroup);
    BigInt inv_mod(const BigInt &m, BN_CTX *ctx) const;
    bool operator==(BigInt &oth);
};

/**
 * @brief Represents elliptic curve points
 * Based on the openssl EC_POINT
 */
class Point
{
public:
    EC_POINT *point = nullptr;
    Group *group = nullptr;
    Point(Group *ECGroup = nullptr);
    ~Point();
    Point(const Point &p);
    Point &operator=(Point p);
    void print();

    void to_bin(unsigned char *buf, size_t buf_len);
    size_t size();
    void from_bin(Group *ECGroup, const unsigned char *buf, size_t buf_len);
    void fromHash(Group *ECGroup, BigInt hash);
    BigInt toHash();

    Point add(Point &Rhs);
    //		Point sub(Point & Rhs);
    //		bool is_at_infinity();
    bool is_on_curve();
    bool is_empty();
    Point mul(const BigInt &m);
    Point inv();
    bool operator==(Point &Rhs);
    bool operator!=(Point &Rhs);
};

/**
 * @brief Represents elliptic curve groups
 * Based on the openssl EC_GROUP
 */
class Group
{
public:
    EC_GROUP *ec_group = nullptr;
    BN_CTX *bn_ctx = nullptr;
    BigInt order;
    unsigned char *scratch;
    size_t scratch_size = 256;
    Group();
    ~Group();
    void resize_scratch(size_t size);
    void get_rand_bn(BigInt &n);
    void get_rand_point(Point &P);
    Point get_generator();
    Point mul_gen(const BigInt &m);
};
