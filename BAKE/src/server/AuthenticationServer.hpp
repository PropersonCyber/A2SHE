#pragma once
#include "core/Core.hpp"

class AuthenticationServer
{
public:
    AuthenticationServer(Group ECGroup);
    Group* getGroup();
    Point sign(Point B, int ks=10);

private:
    Group ECGroup;
    BigInt k;
};