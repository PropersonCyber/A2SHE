#include "ComputationServer.hpp"
#include <iostream>
#include <chrono>
#include <fstream>

ComputationServer::ComputationServer(AuthenticationServer as) : as(as)
{
    ECGroup = as.getGroup();
    ECGroup->get_rand_point(G);

    // Generate server key pair
    KeyPair sk = keygen(G, ECGroup);
    ssk = sk.first;
    Spk = sk.second;
}

Group *ComputationServer::getGroup() { return as.getGroup(); }
Point ComputationServer::getPublicGenerator() { return G; }
BytesVault ComputationServer::getVault(unsigned int id) { return clients[id]->first; }

ServerKeychain ComputationServer::getServerKeychain(unsigned int id, Point Cpk_e, int k)
{
    ServerKeychain keychain;
    keychain.st = false;

    // Recover client stored public key
    Point Cpk_r = clients[id]->second;

    // Generate an exchange key pair
    KeyPair sk_e = keygen(G, ECGroup);
    BigInt ssk_e = sk_e.first;
    Point Spk_e = sk_e.second;

    // Compute final key ks
    auto start = chrono::high_resolution_clock::now();
    BigInt ks = KDF(Cpk_e.mul(ssk_e), Cpk_e.mul(ssk),
                    Cpk_r.mul(ssk_e), Cpk_e,
                    Spk_e, Cpk_r, Spk);

    keychain.Spk = Spk;
    keychain.Spk_e = Spk_e;
    keychain.h_ks = ks.toHash();
    auto stop = chrono::high_resolution_clock::now();

    int t = chrono::duration_cast<chrono::microseconds>(stop - start).count();
    ofstream Out("out/encap_" + to_string(k) + ".chrono", ios_base::app);
    Out << t << endl;
    Out.close();

    return keychain;
}

Point ComputationServer::signToEnroll(BytesVault vault, Point B, unsigned int id, int k)
{
    // TODO store temp vault and id
    return as.sign(B, k);
}

bool ComputationServer::store(BytesVault vault, unsigned int id, Point Cpk_r)
{
    clients[id] = new StoredClient(vault, Cpk_r);
    return true;
}

ServerKeychain ComputationServer::signToVerify(unsigned int id, Point B, Point Cpk_e, int k)
{
    ServerKeychain keychain = getServerKeychain(id, Cpk_e, k);

    keychain.S = as.sign(B, k);
    keychain.st = true;
    return keychain;
}
