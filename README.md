# Instagram Minimal

A patched Instagram APK that blocks ads, suggested content, the feed, stories, explore, reels, and the Notes tray. Built on the [Morphe](https://morphe.software) patching framework.

---

## First-time setup (Arch Linux)

### 1. Download an Instagram APK bundle from APKMirror

Go to:
```
https://www.apkmirror.com/apk/instagram/instagram-instagram/
```
Download the **APKM bundle for arm64-v8a** of a recent version. Make sure it's the bundle format (`.apkm`), not a standalone APK.

> **Version note:** patches are fingerprint-based (they look for stable method shapes and string constants, not hardcoded obfuscated names), so they're generally resilient across updates — but they're written and tested against whatever Instagram version was current at the time. A build that's many versions behind the latest release is more likely to hit a patch whose target code has since been restructured. If a patch fails to match, the fix is usually to grab a newer `.apkm` rather than to chase the old version — see "If the build fails" below.

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
./build.sh "/path/to/your-downloaded-instagram.apkm" --clone --install
```

Replace the path with wherever you saved the `.apkm` from step 1 — copy the exact filename `find` or your file manager shows you, rather than retyping it, since APKMirror filenames are long and easy to mistype.

Drop `--install` to just produce the APK without pushing to a device.
Drop `--clone` to overwrite your existing Instagram install instead of installing side-by-side.

Steps 6–8 (tools, Morphe CLI, the `--purge` fix) are one-time setup — you don't need to redo them for later builds unless you're updating the Morphe CLI itself (see below) or doing a completely fresh clone.

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

### 4. Rebuild and install

```bash
./gradlew :extensions:extension:assembleRelease && \
./gradlew :patches:clean :patches:build && \
rm -f build/merged/*.apk && \
./build.sh "/path/to/new-instagram.apkm" --clone --install
```

(Only rerun `:extensions:extension:assembleRelease` and `:patches:build` if the Java/Kotlin source changed — e.g. after `git pull` or editing a patch yourself. If you're just repointing `build.sh` at a different `.apkm` with the same source, you can skip straight to the `./build.sh` line.)

### 5. Check if any patches broke

Watch the `build.sh` output for lines like `ERROR: FAILED: <patch name>`. If one fails, check `build/patch-report.json` and the full error — it names the specific fingerprint that didn't match and where in the patch's code it's defined. That tells you exactly which patch to look at and why.

---

## If the build fails

- **Fingerprint not found (`ERROR: FAILED: <patch name>`)** — a patch couldn't locate its target method in this Instagram version. First, try a newer `.apkm`: patches are usually written and tested against a fairly recent Instagram version, so an older build is more likely to miss a fingerprint than a current one. If it still fails on a current version, check `build/patch-report.json` for which patch failed and compare its fingerprint strings against the new APK — or skip the failing patch for now with `--continue-on-error` so the rest of the build still produces a working APK while you sort it out.
- **Wrong APK version/format** — make sure you downloaded the bundle format (`.apkm`), not a standalone APK, and that the path passed to `build.sh` is the real file (copy it with `find ~/Downloads -iname '*.apkm'` rather than retyping a long filename).
- **Gradle auth error** — your GitHub token may have expired. Re-run steps 4 and 5 from the setup above.
- **APKEditor missing** — re-run step 6.
