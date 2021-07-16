LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := RetailDemoMode

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v7-appcompat \

LOCAL_SRC_FILES := $(call all-java-files-under,src/)

LOCAL_CERTIFICATE := platform

LOCAL_DEX_PREOPT := false

LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_PRIVATE_PLATFORM_APIS := true

include $(BUILD_PACKAGE)
