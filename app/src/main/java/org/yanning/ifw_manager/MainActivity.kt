package org.yanning.ifw_manager

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import eu.chainfire.libsuperuser.Shell


class MainActivity : AppCompatActivity() {
    private inner class Startup : AsyncTask<Void, Void, Void>() {
        private var dialog: ProgressDialog? = null
        private var context: Context? = null
        private var suAvailable = false
        private var suVersion: String? = null
        private var suVersionInternal: String? = null
        private var suResult: List<String>? = null

        fun setContext(context: Context): Startup {
            this.context = context
            return this
        }

        override fun onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            dialog = ProgressDialog(context)
            dialog!!.setTitle("Some title")
            dialog!!.setMessage("Doing something interesting ...")
            dialog!!.isIndeterminate = true
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        override fun doInBackground(vararg params: Void): Void? {
            // Let's do some SU stuff
            suAvailable = Shell.SU.available()
            if (suAvailable) {
                suVersion = Shell.SU.version(false)
                suVersionInternal = Shell.SU.version(true)
                suResult = Shell.SU.run(arrayOf("id", "ls -l /"))
            }

            // This is just so you see we had a progress dialog,
            // don't do this in production code
            try {
                Thread.sleep(5000)
            } catch (e: Exception) {
            }

            return null
        }

        override fun onPostExecute(result: Void) {
            dialog!!.dismiss()

            // output
            val sb = StringBuilder().append("Root? ").append(if (suAvailable) "Yes" else "No").append(10.toChar())
                .append("Version: ").append(if (suVersion == null) "N/A" else suVersion).append(10.toChar())
                .append("Version (internal): ").append(if (suVersionInternal == null) "N/A" else suVersionInternal)
                .append(10.toChar()).append(10.toChar())
            if (suResult != null) {
                for (line in suResult!!) {
                    sb.append(line).append(10.toChar())
                }
            }
            findViewById<TextView>(R.id.tv_display).text = sb.toString()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Startup().setContext(this).execute()
    }
}
