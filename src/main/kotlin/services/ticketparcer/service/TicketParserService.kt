package services.ticketparcer.service

import io.grpc.stub.StreamObserver
import reactor.core.publisher.Mono
import services.ticketparcer.*
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TicketParserService : TicketParserGrpc.TicketParserImplBase() {

    override fun parseTicket(request: TicketRequest, responseObserver: StreamObserver<TicketResponse>) {
        makeReport(request).map {
            writeTicketToFile(it)
            TicketResponse.newBuilder().setMessage("Report made: $it").build()
        }.subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted)
    }

    override fun increment(request: StreamRequest, responseObserver: StreamObserver<StreamResponse>) {
        for (i in 0..10) {
            val response = StreamResponse.newBuilder()
                .setMessage("${request.name}: ${request.count + i}")
                .build()
            responseObserver.onNext(response)
        }
        responseObserver.onCompleted()
    }

    private fun makeReport(request: TicketRequest): Mono<String> {
        val report = Mono.just(buildString {
            append("Ticket:\n")
                .append("Name: ${request.username}")
                .append("\n")
                .append("Movie: ${request.movieTitle}")
                .append("\n")
                .append("Hall: ${request.cinemaHallDescription}")
                .append("\n")
                .append("Time: ${formatDate(LocalDateTime
                    .ofEpochSecond(request.showtime, 0,  ZoneOffset.UTC))}")
        })
        return report
    }

    private fun writeTicketToFile(report: String) {
        File("/Users/tlogatskaya/IdeaProjects/cinema/services/ticket-parcer/src/main/resources/ticket.txt")
            .writeText(report, Charset.forName("UTF-8"))
    }

    private fun formatDate(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
    }
}