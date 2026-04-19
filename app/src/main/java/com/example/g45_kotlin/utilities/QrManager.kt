package com.example.g45_kotlin.utilities
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter


fun generateQrCodeBitmap(content: String): Bitmap? {
    val size = 512
    try {
        val hints = HashMap<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[y, x] = if (bitMatrix[y, x]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}