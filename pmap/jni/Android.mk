LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Map
LOCAL_SRC_FILES := Map.cpp MapDraw.cpp Polygon.cpp Layer.cpp
LOCAL_CFLAGS    := -Werror
LOCAL_LDLIBS	:= -llog -lGLESv1_CM
LOCAL_C_INCLUDES := D:\YanXiao\Nexd\Workspace_Code\pmap\jni
include $(BUILD_SHARED_LIBRARY)
