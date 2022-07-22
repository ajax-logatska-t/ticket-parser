package services.ticketparcer

import io.grpc.ServerBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import services.ticketparcer.service.MovieStreamingService
import services.ticketparcer.service.ReactiveTicketParserService
import services.ticketparcer.service.TicketParserService

@SpringBootApplication
class TicketParcerApplication

fun main(args: Array<String>) {
    runApplication<TicketParcerApplication>(*args)

    val server = ServerBuilder.forPort(8082)
        .addService(TicketParserService())
        .addService(ReactiveTicketParserService())
        .addService(MovieStreamingService())
        .build()

    server.start()
    server.awaitTermination()
}
