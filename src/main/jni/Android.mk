LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := curl
LOCAL_SRC_FILES := ../../../libs/libcurl.a
#LOCAL_PREBUILT_LIBS := ./curllib/libcurl.so
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := getDevice
LOCAL_CFLAGS := -DLSB -fvisibility=hidden
LOCAL_LDLIBS := \
	-llog \
#LOCAL_STATIC_LIBRARIES :=libcurl.a
LOCAL_SHARED_LIBRARIES := libcurl
LOCAL_SRC_FILES := \
	Android.mk \
	GetInfo.cpp \
	Application.mk \
	MacGet.cpp \
	Build.cpp \
	OtherParams.cpp \
	TM.cpp \
	Token_Z.cpp \
	WM.cpp \
	Utils.cpp \
	Helper.cpp \
	MyCts.cpp \
	iccatch/IcCore.cpp \
	json/JSON.cpp \
    json/JSONValue.cpp \

include $(BUILD_SHARED_LIBRARY)
