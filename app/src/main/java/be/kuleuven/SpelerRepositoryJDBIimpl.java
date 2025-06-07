
package be.kuleuven;

import org.jdbi.v3.core.Jdbi;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpelerRepositoryJDBIimpl implements SpelerRepository {
  private final Jdbi jdbi;

  // Constructor
  SpelerRepositoryJDBIimpl(String connectionString, String user, String pwd) {
    
    this.jdbi = Jdbi.create(connectionString, user, pwd);;
  }

      
  @Override
  public void addSpelerToDb(Speler speler) {
  if (speler == null) {
     throw new IllegalArgumentException("Speler mag niet null zijn");
    }

    String sql = "INSERT INTO speler (tennisvlaanderenid, naam, punten) VALUES (:id, :naam, :punten)";
    jdbi.useHandle(handle -> handle.createUpdate(sql)
       .bind("id", speler.getTennisvlaanderenId())
       .bind("naam", speler.getNaam())
       .bind("punten", speler.getPunten())
       .execute());
  }

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    String sql = "SELECT tennisvlaanderenid, naam, punten FROM speler WHERE tennisvlaanderenid = :id";
    return jdbi.withHandle(handle -> handle.createQuery(sql)
        .bind("id", tennisvlaanderenId)
        .map((rs, ctx) -> new Speler(
            rs.getInt("tennisvlaanderenid"),
            rs.getString("naam"),
            rs.getInt("punten")))
        .findOne()
        .orElseThrow(() -> new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenId)));
  }

  @Override
  public List<Speler> getAllSpelers() {
    String sql = "SELECT tennisvlaanderenid, naam, punten FROM speler";
    return jdbi.withHandle(handle -> handle.createQuery(sql)
        .map((rs, ctx) -> new Speler(
            rs.getInt("tennisvlaanderenid"),
            rs.getString("naam"),
            rs.getInt("punten")))
        .list());
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    if (speler == null) {
      throw new IllegalArgumentException("Speler mag niet null zijn");
    }

    String sql = "UPDATE speler SET naam = :naam, punten = :punten WHERE tennisvlaanderenid = :id";
    int rowsUpdated = jdbi.withHandle(handle -> handle.createUpdate(sql)
        .bind("naam", speler.getNaam())
        .bind("punten", speler.getPunten())
        .bind("id", speler.getTennisvlaanderenId())
        .execute());

    if (rowsUpdated == 0) {
      throw new RuntimeException("Invalid Speler met identification: " + speler.getTennisvlaanderenId());
    }
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenid) {
    String sql = "DELETE FROM speler WHERE tennisvlaanderenid = :id";
    int rowsDeleted = jdbi.withHandle(handle -> handle.createUpdate(sql)
        .bind("id", tennisvlaanderenid)
        .execute());

    if (rowsDeleted == 0) {
      throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenid);
    }
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenid) {
    String sql = """
        SELECT t.clubnaam, w.finale, w.winnaar
        FROM wedstrijd w
        JOIN tornooi t ON w.tornooi = t.id
        WHERE (w.speler1 = :id OR w.speler2 = :id OR w.winnaar = :id)
        ORDER BY 
            CASE 
                WHEN w.finale = 1 THEN 1
                WHEN w.finale = 2 THEN 2
                ELSE 3
            END
        LIMIT 1
    """;

    return jdbi.withHandle(handle -> handle.createQuery(sql)
        .bind("id", tennisvlaanderenid)
        .map((rs, ctx) -> {
          String clubnaam = rs.getString("clubnaam");
          int finale = rs.getInt("finale");
          int winnaar = rs.getInt("winnaar");

          String plaats = switch (finale) {
            case 1 -> (winnaar == tennisvlaanderenid) ? "de winst" : "de finale";
            case 2 -> "de halve finale";
            default -> "de kwartfinale of lager";
          };

          return "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in " + plaats;
        })
        .findOne()
        .orElseThrow(() -> new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenid)));
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
      // Controleer of de speler bestaat
      String spelerCheckSql = "SELECT COUNT(*) FROM speler WHERE tennisvlaanderenid = :speler";
      boolean spelerExists = jdbi.withHandle(handle -> handle.createQuery(spelerCheckSql)
          .bind("speler", tennisvlaanderenId)
          .mapTo(Boolean.class)
          .findOne()
          .orElse(false));
  
      if (!spelerExists) {
          throw new RuntimeException("Speler met ID " + tennisvlaanderenId + " bestaat niet.");
      }
  
      // Controleer of het tornooi bestaat
      String tornooiCheckSql = "SELECT COUNT(*) FROM tornooi WHERE id = :tornooi";
      boolean tornooiExists = jdbi.withHandle(handle -> handle.createQuery(tornooiCheckSql)
          .bind("tornooi", tornooiId)
          .mapTo(Boolean.class)
          .findOne()
          .orElse(false));
  
      if (!tornooiExists) {
          throw new RuntimeException("Tornooi met ID " + tornooiId + " bestaat niet.");
      }
  
      // Voeg de speler toe aan het tornooi
      String sql = "INSERT INTO speler_speelt_tornooi (speler, tornooi) VALUES (:speler, :tornooi)";
      jdbi.useHandle(handle -> handle.createUpdate(sql)
          .bind("speler", tennisvlaanderenId)
          .bind("tornooi", tornooiId)
          .execute());
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    String sql = "DELETE FROM speler_speelt_tornooi WHERE speler = :speler AND tornooi = :tornooi";
    int rowsDeleted = jdbi.withHandle(handle -> handle.createUpdate(sql)
        .bind("speler", tennisvlaanderenId)
        .bind("tornooi", tornooiId)
        .execute());

    if (rowsDeleted == 0) {
      throw new RuntimeException("Geen speler gevonden met ID: " + tennisvlaanderenId + " in tornooi: " + tornooiId);
    }
  }
}
