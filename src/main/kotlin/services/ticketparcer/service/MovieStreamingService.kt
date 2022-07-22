package services.ticketparcer.service

import io.nats.client.Nats
import proto.cinema.MovieRequest
import proto.cinema.MovieResponse
import proto.cinema.ReactorMovieStreamingGrpc
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.random.Random

class MovieStreamingService: ReactorMovieStreamingGrpc.MovieStreamingImplBase() {
    override fun streamMovie(request: Mono<MovieRequest>): Flux<MovieResponse> {
        val nc = Nats.connect()
        val dispatcher = nc.createDispatcher()
        val subscription = Flux.create { sink: FluxSink<ByteArray> ->
            dispatcher.subscribe("movies") { sink.next(it.data) }
        }.map { String(it)}
        return request.cache().repeat()
            .delayElements(Duration.ofSeconds(1))
            .map { it.getTitle(Random.nextInt(it.titleCount)) }
            .mergeWith(subscription)
                .map {
                    MovieResponse.newBuilder()
                        .setMessage(it)
                        .build()
                }.doOnError { println(it) }
    }
}