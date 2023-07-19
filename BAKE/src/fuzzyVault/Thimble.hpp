#pragma once
#include "FJFXFingerprint.hpp"

MinutiaeView getMinutiaeView(string image);
BytesVault fuzzyVault2Bytes(ProtectedMinutiaeTemplate vault);
ProtectedMinutiaeTemplate bytes2FuzzyVault(BytesVault data);
uint32_t getf0(BytesVault bVault, MinutiaeView view);

// Values for the mcyt database
const int mcytWidth(256);
const int mcytHeight(400);
const int mcytDpi(500);