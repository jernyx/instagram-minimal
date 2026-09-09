# 1. Download Instagram APK bundle from APKMirror

Go to https://www.apkmirror.com/apk/instagram/instagram-instagram/instagram-444-0-0-46-85-release/ and download the APKM bundle for arm64-v8a. Make sure it's the bundle format (.apkm), not a standalone APK. If the build fails later, try a different Instagram version from APKMirror.

# 2. Install dependencies (archlinux)
sudo pacman -S github-cli

# 3. Login to GitHub (pick "Login with a web browser" when prompted)
gh auth login --scopes read:packages

# 4. Save credentials for Gradle
mkdir -p ~/.gradle
cat >> ~/.gradle/gradle.properties << EOF
gpr.user=$(gh api user --jq .login)
gpr.key=$(gh auth token)
EOF

# 5. Create tools dir and download APKEditor
mkdir -p tools/
wget -O tools/APKEditor-1.4.9.jar \
     https://github.com/REAndroid/APKEditor/releases/download/V1.4.9/APKEditor-1.4.9.jar

# 6. Download Morphe CLI
wget -O tools/morphe-cli-1.14.0.jar \
     https://github.com/MorpheApp/morphe-desktop/releases/download/v1.14.0/morphe-desktop-1.14.0-all.jar

# 7. Fix outdated --purge flag in build script
sed -i 's/--purge/--disable-purge/' build.sh

# 8. Build and install

./gradlew :extensions:extension:assembleRelease && ./gradlew :patches:clean :patches:build && rm -f build/merged/*.apk && ./build.sh "/Downloads/com.instagram.android_444.0.0.46.85-385010792_1dpi_7171e286f3d273174955e1d8678239ea_apkmirror.com.apkm" --clone --install
