#include "AuthenticationServer.hpp"
#include <chrono>
#include <iostream>
#include <fstream>

AuthenticationServer::AuthenticationServer(Group ECGroup) : ECGroup(ECGroup)
{
    // TODO: Is that random function secure?
    ECGroup.get_rand_bn(k);
}

Group *AuthenticationServer::getGroup() { return &ECGroup; }

Point AuthenticationServer::sign(Point B, int ks)
{
    auto start = chrono::high_resolution_clock::now();
    Point S = signPoint(B,k);
    auto stop = chrono::high_resolution_clock::now();

    int t = chrono::duration_cast<chrono::microseconds>(stop - start).count();
    ofstream Out("out/sign_" + to_string(ks) + ".chrono", ios_base::app);
    Out << t << endl;
    Out.close();
    
    return S;
}