package com.example.manager

import android.content.Context
import android.provider.ContactsContract

data class ContactInfo(
    val name: String,
    val phoneNumber: String
)

object ContactManager {

    fun searchContactsByName(context: Context, query: String): List<ContactInfo> {
        if (!PermissionManager.isContactsGranted(context)) {
            return emptyList()
        }

        val results = mutableListOf<ContactInfo>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = if (nameIdx != -1) it.getString(nameIdx) else ""
                    val number = if (numberIdx != -1) it.getString(numberIdx) else ""
                    if (name.isNotBlank() && number.isNotBlank()) {
                        results.add(ContactInfo(name, number.replace(" ", "").replace("-", "")))
                    }
                }
            }
        } catch (e: Exception) {
            // Permission or security exception
        }
        return results
    }
}
