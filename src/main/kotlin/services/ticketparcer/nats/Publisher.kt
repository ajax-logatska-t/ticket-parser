package services.ticketparcer.nats

import io.nats.client.Nats

fun main() {
    try {
        Nats.connect().use { nc ->
            print("About to publish...")
            nc.publish("subject", "Test message".toByteArray())
            println("Done.")
        }
    } catch (exp: Exception) {
        exp.printStackTrace()
    }
}