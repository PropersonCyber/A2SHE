#pragma once

#include <openssl/ec.h>
#include <openssl/bn.h>
#include <openssl/obj_mac.h>
#include <utility>
#include "ec.hpp"
#include "fuzzyVault/Thimble.hpp"

using KeyPair = std::pair<BigInt, Point>;
using BlindedPair = std::pair<Point, BigInt>;

BlindedPair blind(const BigInt x, Group* ECGroup);
Point unblind(const Point B, const BigInt r);
BigInt KDF(Point K1, Point K2, Point K3, Point K4, Point K5, Point K6, Point K7);
KeyPair keygen(Point P, Group *ECGroup);
Point getPublicKey(BigInt sk, Point P);
Point signPoint(Point P, BigInt k);
