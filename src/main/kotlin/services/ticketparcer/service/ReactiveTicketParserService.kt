package services.ticketparcer.service

import proto.cinema.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.io.File
import java.nio.charset.Charset
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ReactiveTicketParserService : ReactorTicketParserGrpc.TicketParserImplBase() {
    override fun parseTicket(request: Mono<TicketRequest>): Mono<TicketResponse> {
        return makeReport(request)
            .map {
            writeTicketToFile(it)
                TicketResponse.newBuilder().setMessage("Report built: $it").build()
            }
    }

    override fun increment(request: Mono<StreamRequest>): Flux<StreamResponse> {
        return Flux.interval(Duration.ofSeconds(1))
            .zipWith(request.cache().repeat())
            .map {
                StreamResponse
                    .newBuilder()
                    .setMessage("${it.t2.name}: ${it.t2.count + it.t1}")
                    .build()
            }
    }

    private fun makeReport(request: Mono<TicketRequest>): Mono<String> {
        val report = request.map {
            buildString {
                append("Ticket:\n")
                    .append("Name: ${it.username}")
                    .append("\n")
                    .append("Movie: ${it.movieTitle}")
                    .append("\n")
                    .append("Hall: ${it.cinemaHallDescription}")
                    .append("\n")
                    .append("Time: ${formatDate(
                        LocalDateTime
                            .ofEpochSecond(it.showtime, 0,  ZoneOffset.UTC))}")
            }
        }
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