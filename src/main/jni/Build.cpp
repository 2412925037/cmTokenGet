#include "Build.h"
#include "Utils.h"
int   property_get(const char *key, char *value, const char *default_value)
{
    int len;
    len = __system_property_get(key, value);
    if(*value==0){
    	 strcpy(value, default_value);
    }
    return len;
}

string   getModel(){
	if(logShow)LOGI("getModel...");
	 char char2 [PROP_VALUE_MAX];
	 property_get("ro.product.model",char2,"none");
	return string(char2);
}
string   getSdkVersion(){
	if(logShow)LOGI("getSdkVersion...");
	 char char2 [PROP_VALUE_MAX];
	 property_get("ro.build.version.sdk",char2,"none");
	 return string(char2);
}
string   getBrand(){
	if(logShow)	LOGI("getBrand...");
	 char char2 [PROP_VALUE_MAX];
			 property_get("ro.product.brand",char2,"none");
	 return string(char2);
}
string  getRelease(){
	if(logShow)LOGI("getRelease...");
	char char2 [PROP_VALUE_MAX];
				 property_get("ro.build.version.release",char2,"none");
		 return string(char2);
}
string  getCpu(){
	if(logShow)LOGI("getCpu...");
	char char2 [PROP_VALUE_MAX];
					 property_get("ro.product.cpu.abi",char2,"none");
			 return string(char2);
}
