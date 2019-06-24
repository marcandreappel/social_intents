package `fun`.appel.socialintents

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

class SocialIntents(private val context: Context) {

    private val snKeywords = arrayListOf(
        "vk",
        "instagram",
        "twitter",
        "youtube",
        "facebook",
        "linkedin"
    )
    private val snPackages = linkedMapOf(
        "vk" to "com.vkontakte.android",
        "instagram" to "com.instagram.android",
        "twitter" to "com.twitter.android",
        "youtube" to "com.google.android.youtube",
        "facebook" to "com.facebook.katan",
        "linkedin" to "com.linkedin.android"
    )
    private val snIntents = linkedMapOf(
        "vk" to "vkontakte://group/",
        "instagram" to "http://instagram.com/_u/",
        "twitter" to "twitter://user?screen_name=",
        "youtube" to "http://www.youtube.com/channel/",
        "facebook" to "fb://profile/",
        "linkedin" to "linkedin://profile/"
    )
    private val snUrls = linkedMapOf(
        "vk" to "https://m.vk.com/",
        "instagram" to "https://www.instagram.com/",
        "twitter" to "https://twitter.com/",
        "youtube" to "https://www.youtube.com/user/",
        "facebook" to "https://facebook.com/",
        "linkedin" to "https://www.linkedin/in/"
    )

    private lateinit var snAlias: String
    private lateinit var snPackage: String
    private lateinit var snUrl: String

    fun setUrl(url: String): SocialIntents {
        this.snUrl = url
        return this
    }

    /**
     * Fails silently
     */
    fun start() {
        val intent = build()
        if (intent != null) {
            context.startActivity(intent)
        }
    }

    /**
     * @return Intent
     */
    private fun build(): Intent? {
        if (::snUrl.isInitialized) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getIntent(context, snUrl)))

            if (isPackage(context, snUrl)) {
                intent.setPackage(snPackage)
            }
            return intent
        }
        return null
    }

    /**
     * @param context
     * @param url
     *
     * @return String|null
     */
    private fun getIntent(context: Context, url: String?): String? {
        for (index in 0 until snKeywords.size) {
            val alias = snKeywords[index]

            if (url!!.contains(alias)) {
                this.snAlias = alias

                val socialPackage = snPackages[alias]
                val socialName = url.substring(url.lastIndexOf('/') + 1)

                return if (isPackage(context, socialPackage)) {
                    snPackage = socialPackage!!
                    "${snIntents[alias]}$socialName"
                } else {
                    "${snUrls[alias]}$socialName"
                }
            }
        }
        return url
    }

    /**
     * @param context Application context
     * @param socialPackage A string with the known package name of one of the social networks
     *
     * @return Boolean indicating if a certain package is installed on the terminal
     */
    private fun isPackage(context: Context, socialPackage: String?): Boolean {
        val packageManager = context.packageManager

        try {
            packageManager.getPackageInfo(socialPackage, PackageManager.GET_ACTIVITIES)
        } catch (exception: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }
}

fun Context.startSocialActivity(socialNetworkIntent: String) = SocialIntents(this).setUrl(socialNetworkIntent).start()
