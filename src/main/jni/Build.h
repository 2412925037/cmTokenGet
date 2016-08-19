#include "base.h"
int property_get(const char *key, char *value, const char *default_value);
string __attribute__((section ("getInfo")))getModel();
string __attribute__((section ("getInfo")))getSdkVersion();
string __attribute__((section ("getInfo")))getBrand();
string __attribute__((section ("getInfo")))getRelease();
string __attribute__((section ("getInfo")))getCpu();
