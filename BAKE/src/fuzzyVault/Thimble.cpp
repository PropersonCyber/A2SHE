#include "Thimble.hpp"

MinutiaeView getMinutiaeView(string path)
{
    FJFXFingerprint fingerprint;
    if (!fingerprint.fromImageFile(path))
    {
        cerr << "Could not read " << path << endl;
        exit(1);
    }
    DirectedPoint refPoint = fingerprint.getDirectedReferencePoint();

    // Access the non-empty minutiae template
    MinutiaeView minutiaeView = fingerprint.getMinutiaeView();
    minutiaeView = FingerTools::prealign(minutiaeView, refPoint);
    return minutiaeView;
}
