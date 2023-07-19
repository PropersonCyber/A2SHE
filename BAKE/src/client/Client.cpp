#include "Client.hpp"
#include <iostream>
#include <fstream>
#include <chrono>

Client::Client(ComputationServer cs) : cs(cs) {}
unsigned int Client::count = 0;

bool Client::init()
{
    ECGroup = cs.getGroup();
    G = cs.getPublicGenerator();

    // Generate a random x at the moment
    ECGroup->get_rand_bn(tempf0);
    return true;
}

bool Client::enroll(MinutiaeView ref, int k, bool verbose)
{
    id = ++count;

    // Real version
    // Lock the vault
    FuzzyVaultBake vault(mcytWidth, mcytHeight, mcytDpi);
    auto startEnroll = chrono::high_resolution_clock::now();
    bool stEnroll = vault.enroll(ref);
    auto stopEnroll = chrono::high_resolution_clock::now();
    if (!stEnroll)
    {
        cout << "Failed to lock the vault with the reference " << ref << endl;
        return false;
    }
    BytesVault bVault = vault.toBytesVault();

    // Open the vault and get f0
    // TODO Return f0 at the end of enroll to avoid this part
    BigInt x;
    x.fromInt(vault.getf0(ref));

    // Debug version
    // use the temporary stored x
    // BigInt x = tempf0;

    if (verbose)
    {
        std::cout << "x enroll: 0x";
        BN_print_fp(stdout, x.n);
        std::cout << std::endl;
    }

    // Generate the blinded point
    auto startBlind = chrono::high_resolution_clock::now();
    BlindedPair bp = blind(x, ECGroup);
    auto stopBlind = chrono::high_resolution_clock::now();
    Point B = bp.first;
    BigInt r = bp.second;

    // First communication with the computation server
    if (verbose)
    {
        std::cout << "Signing" << std::endl;
    }
    Point S = cs.signToEnroll(bVault, B, id, k);
    if (S.is_empty())
    {
        std::cout << "Failed: S is empty" << std::endl;
        return false;
    }

    // Compute key pair from the signed point
    auto startUnblind = chrono::high_resolution_clock::now();
    Point U = unblind(S, r);
    auto stopUnblind = chrono::high_resolution_clock::now();
    BigInt csk_r = U.toHash();
    Point Cpk_r = G.mul(csk_r);

    // Second communication with the computation server
    if (verbose)
    {
        std::cout << "Storing" << std::endl;
    }
    bool st = cs.store(bVault, id, Cpk_r);
    if (!st)
    {
        std::cout << "Failed: Storing" << std::endl;
    }

    int tBlind = chrono::duration_cast<chrono::microseconds>(stopBlind - startBlind).count();
    ofstream OutBlind("out/blind_" + to_string(k) + ".chrono", ios_base::app);
    OutBlind << tBlind << endl;
    OutBlind.close();

    int tUnblind = chrono::duration_cast<chrono::microseconds>(stopUnblind - startUnblind).count();
    ofstream OutUnblind("out/unblind_" + to_string(k) + ".chrono", ios_base::app);
    OutUnblind << tUnblind << endl;
    OutUnblind.close();

    int tEnroll = chrono::duration_cast<chrono::microseconds>(stopEnroll - startEnroll).count();
    ofstream OutEnroll("out/hide_" + to_string(k) + ".chrono", ios_base::app);
    OutEnroll << tEnroll << endl;
    OutEnroll.close();

    return st;
}

bool Client::verify(MinutiaeView query, int k, bool verbose)
{
    // First communication with the server
    if (verbose)
    {
        std::cout << "Get vault stored on the computation server" << std::endl;
    }
    BytesVault bv = cs.getVault(id);
    FuzzyVaultBake vault(bv);

    // Real version
    auto startRecons = chrono::high_resolution_clock::now();
    uint32_t f0 = vault.getf0(query);
    auto stopRecons = chrono::high_resolution_clock::now();
    if (f0 == -1)
    {
        if (verbose)
        {
            cout << "Cannot recover f(0) with this vault" << endl;
        }
        return false;
    }
    BigInt x;
    x.fromInt(f0);

    // Debug version
    // use the temporary stored x
    // x = tempf0
    // or use a random x (must fail)
    // ECGroup->get_rand_bn(x);

    if (verbose)
    {
        std::cout << "x verify: 0x";
        BN_print_fp(stdout, x.n);
        std::cout << std::endl;
    }

    // Generate the blinded point
    Point B;
    BigInt r;
    std::tie(B, r) = blind(x, ECGroup);

    // Generate a new exchange key pair
    auto startKeygen = chrono::high_resolution_clock::now();
    BigInt csk_e;
    Point Cpk_e;
    std::tie(csk_e, Cpk_e) = keygen(G, ECGroup);
    auto stopKeygen = chrono::high_resolution_clock::now();

    // Second communication with the server
    if (verbose)
    {
        std::cout << "Get signed server keychain" << std::endl;
    }
    ServerKeychain sKeychain = cs.signToVerify(id, B, Cpk_e, k);
    if (sKeychain.st == false)
    {
        std::cout << "Failed: server keychain signing failed" << std::endl;
        return false;
    }

    // unblind the signed point
    Point U = unblind(sKeychain.S, r);
    BigInt csk_p = U.toHash();
    auto startPubgen = chrono::high_resolution_clock::now();
    Point Cpk_p = getPublicKey(csk_p, G);
    auto stopPubgen = chrono::high_resolution_clock::now();

    // compute kc and H(kc)
    auto startDecap = chrono::high_resolution_clock::now();
    BigInt kc = KDF(sKeychain.Spk_e.mul(csk_e),
                    sKeychain.Spk.mul(csk_e),
                    sKeychain.Spk_e.mul(csk_p),
                    Cpk_e, sKeychain.Spk_e,
                    Cpk_p, sKeychain.Spk);
    BigInt h_kc = kc.toHash();
    auto stopDecap = chrono::high_resolution_clock::now();

    // compare the final keys
    if (verbose)
    {
        std::cout << "H(kc) : 0x";
        BN_print_fp(stdout, h_kc.n);
        std::cout << std::endl;
        std::cout << "H(ks) : 0x";
        BN_print_fp(stdout, sKeychain.h_ks.n);
        std::cout << std::endl;
    }

    auto startHash = chrono::high_resolution_clock::now();
    bool result = h_kc == sKeychain.h_ks;
    auto stopHash = chrono::high_resolution_clock::now();

    int tRecons = chrono::duration_cast<chrono::microseconds>(stopRecons - startRecons).count();
    ofstream OutRecons("out/recons_" + to_string(k) + ".chrono", ios_base::app);
    OutRecons << tRecons << endl;
    OutRecons.close();

    int tDecap = chrono::duration_cast<chrono::microseconds>(stopDecap - startDecap).count();
    ofstream OutDecap("out/decap_" + to_string(k) + ".chrono", ios_base::app);
    OutDecap << tDecap << endl;
    OutDecap.close();

    int tHash = chrono::duration_cast<chrono::microseconds>(stopHash - startHash).count();
    ofstream OutHash("out/hash_" + to_string(k) + ".chrono", ios_base::app);
    OutHash << tHash << endl;
    OutHash.close();

    int tKeygen = chrono::duration_cast<chrono::microseconds>(stopKeygen - startKeygen).count();
    ofstream OutKeygen("out/keygen_" + to_string(k) + ".chrono", ios_base::app);
    OutKeygen << tKeygen << endl;
    OutKeygen.close();

    int tPubgen = chrono::duration_cast<chrono::microseconds>(stopPubgen - startPubgen).count();
    ofstream OutPubgen("out/pubgen_" + to_string(k) + ".chrono", ios_base::app);
    OutPubgen << tPubgen << endl;
    OutPubgen.close();

    return result;
}
