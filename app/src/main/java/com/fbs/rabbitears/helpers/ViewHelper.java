package com.fbs.rabbitears.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.fbs.rabbitears.R;

/**
 * View Helper Methods
 */
public class ViewHelper
{
    private static final String    HTML_DOC_START;
    private static final String    HTML_DOC_END;

    /**
     * Static Initializer
     */
    static
    {
        HTML_DOC_START = new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("    <head>\n")
                .append("        <title>RSS Description</title>\n")
                .append("        <meta charset=\"UTF-8\">")
                .append("        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">")
                .append("    </head>\n")
                .append("    <body>\n")
                .append("        \n")
                .toString();

        HTML_DOC_END = new StringBuilder()
                .append("        \n")
                .append("    </body>\n")
                .append("</html>")
                .toString();
    }

    /**
     * Wraps a body string in an HTML5 document
     * @param bodyContent String content of document body
     * @return String HTML document
     */
    public static String encaseHtmlDoc(String bodyContent)
    {
        return new StringBuilder()
                .append(HTML_DOC_START)
                .append(bodyContent)
                .append(HTML_DOC_END)
                .toString();
    }

    /**
     * Generate confirmation dialog
     * @param question String questioning message to display
     * @param listener OnClickListener click event handler for dialog
     * @param parent Activity parent
     * @return Builder confirmation dialog
     */
    public static AlertDialog.Builder confirmDialog(String question, DialogInterface.OnClickListener listener, Activity parent)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        return builder.setMessage(question)
                .setPositiveButton(parent.getResources().getString(R.string.option_yes), listener)
                .setNegativeButton(parent.getResources().getString(R.string.option_no),  listener);
    }

    /**
     * Generate confirmation dialog
     * @param message String information message to display
     * @param listener OnClickListener click event handler for dialog
     * @param parent Activity parent
     * @return Builder information dialog
     */
    public static AlertDialog.Builder infoDialog(String message, DialogInterface.OnClickListener listener, Activity parent)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        return builder.setMessage(message)
                .setNeutralButton(parent.getResources().getString(R.string.option_ok), listener);
    }
}
