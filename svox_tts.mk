include external/svox/pico/lang/all_pico_languages.mk

PRODUCT_PACKAGES += \
    PicoTts

PRODUCT_COPY_FILES += \
    external/svox/pico/default_permissions_picotts.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/default-permissions/default_permissions_picotts.xml
