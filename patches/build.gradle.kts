group = "com.instagram"

patches {
    about {
        name = "Instagram Patches"
        description = "Instagram mod: blocks the feed, stories, explore, reels, ads, suggestions and much more."
        source = "git@github.com:jernyx/Instagram.git"
        author = "Jernyx"
        contact = "na"
        website = "na"
        license = "GPLv3"
    }
}

dependencies {
    // Provides app.morphe.util.* helpers (DOM, bytecode, references) used by the patches.
    implementation(libs.morphe.patches.library)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
