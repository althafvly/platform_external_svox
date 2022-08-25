include external/svox/pico/lang/all_pico_languages.mk

PRODUCT_PACKAGES += \
    PicoTts

PRODUCT_COPY_FILES += \
    external/svox/pico/privapp_whitelist_picotts.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/privapp_whitelist_picotts.xml
