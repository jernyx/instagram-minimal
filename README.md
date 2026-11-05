# Instagram Minimal

A patched Instagram APK that blocks ads, suggested content, the feed, stories, explore, and reels. Built on the [Morphe](https://morphe.software) patching framework.

Currently targets **Instagram 454.0.0.13.77**.

---

## First-time setup (Arch Linux)

### 1. Download the Instagram APK bundle from APKMirror

Go to:
```
https://www.apkmirror.com/apk/instagram/instagram-instagram/instagram-454-0-0-13-77-release/
```
Download the **APKM bundle for arm64-v8a**. Make sure it's the bundle format (`.apkm`), not a standalone APK. If the build fails later, try a different Instagram version from APKMirror.

### 2. Install dependencies

```bash
sudo pacman -S github-cli
```

### 3. Clone this repo

```bash
git clone https://github.com/jernyx/instagram-minimal.git
cd instagram-minimal
```

### 4. Log in to GitHub

```bash
gh auth login --scopes read:packages
```
Pick "Login with a web browser" when prompted.

### 5. Save credentials for Gradle

```bash
mkdir -p ~/.gradle
cat >> ~/.gradle/gradle.properties << EOF
gpr.user=$(gh api user --jq .login)
gpr.key=$(gh auth token)
EOF
```

### 6. Download APKEditor

```bash
mkdir -p tools/
gh release download V1.4.9 -R REAndroid/APKEditor -p 'APKEditor-1.4.9.jar' -D tools/
```

### 7. Download Morphe CLI

Check the latest release filename at https://github.com/MorpheApp/morphe-desktop/releases and download it into `tools/`. For example:
```bash
wget -O tools/morphe-desktop-1.19.0.jar \
     https://github.com/MorpheApp/morphe-desktop/releases/download/v1.19.0/morphe-desktop-1.19.0-all.jar
```

### 8. Fix the `--purge` flag in the build script

```bash
grep -q "disable-purge" build.sh || sed -i 's/--purge/--disable-purge/' build.sh
```

### 9. Build and install

```bash
./gradlew :extensions:extension:assembleRelease && \
./gradlew :patches:clean :patches:build && \
rm -f build/merged/*.apk && \
./build.sh "/path/to/com.instagram.android_454.0.0.13.77-*.apkm" --clone --install
```

Replace the path with wherever you saved the `.apkm` from step 1.  
Drop `--install` to just produce the APK without pushing to a device.  
Drop `--clone` to overwrite your existing Instagram install instead of installing side-by-side.

---

## Updating to a newer Instagram version

When a new Instagram version comes out, the patches may need updating to match changes in the app's internals. Here is the full process.

### 1. Pull the latest code

```bash
git pull
```

### 2. Download the new Instagram APK bundle

Go to APKMirror and find the new version:
```
https://www.apkmirror.com/apk/instagram/instagram-instagram/
```
Download the **APKM bundle for arm64-v8a**.

### 3. Update the Morphe CLI if needed

Check https://github.com/MorpheApp/morphe-desktop/releases for a newer version and download it into `tools/` the same way as step 7 above. The build script picks up the newest jar automatically.

### 4. Check if any patches broke

The patches use structural fingerprints (stable method signatures and string constants) rather than hardcoded obfuscated names, so most Instagram updates require no patch changes at all. If the build fails with a fingerprint error, check `build/patch-report.json` — it shows exactly which patch failed to match and why.

### 5. Rebuild and install

```bash
./gradlew :extensions:extension:assembleRelease && \
./gradlew :patches:clean :patches:build && \
rm -f build/merged/*.apk && \
./build.sh "/path/to/new-instagram.apkm" --clone --install
```

---

## If the build fails

- **Fingerprint not found** — a patch couldn't locate its target method in this Instagram version. Check `build/patch-report.json` for which patch failed, then compare the patch's fingerprint strings against the new APK.
- **Wrong APK version** — make sure you downloaded the correct version and the bundle format (`.apkm`), not a standalone APK.
- **Gradle auth error** — your GitHub token may have expired. Re-run steps 4 and 5 from the setup above.
- **APKEditor missing** — re-run step 6.
