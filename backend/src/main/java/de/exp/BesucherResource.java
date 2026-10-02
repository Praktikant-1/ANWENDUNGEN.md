package de.exp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.ArrayList;
import java.util.List;

@Path("/besucher")
public class BesucherResource {

    private final List<Besucher> besucherListe = new ArrayList<>();

    @GET
    public List<Besucher> alle() {
        return besucherListe;
    }

    @POST
    public Besucher hinzufuegen(Besucher besucher) {
        besucherListe.add(besucher);
        return besucher;
    }
}
