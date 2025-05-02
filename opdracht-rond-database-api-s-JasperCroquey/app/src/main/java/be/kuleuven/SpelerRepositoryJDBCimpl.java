package be.kuleuven;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SpelerRepositoryJDBCimpl implements SpelerRepository {
  private Connection connection;

  // Constructor
  SpelerRepositoryJDBCimpl(Connection connection) {
  if (connection == null) {
      throw new IllegalArgumentException("Connection cannot be null");
  }
  this.connection = connection;
}
@Override
public void addSpelerToDb(Speler speler) {
    if (speler == null) {
        throw new IllegalArgumentException("Speler mag niet null zijn");
    }

    String sql = "INSERT INTO speler (tennisvlaanderenid, naam, punten) VALUES (?, ?, ?)";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, speler.getTennisvlaanderenid());
        preparedStatement.setString(2, speler.getNaam());
        preparedStatement.setInt(3, speler.getPunten());
        preparedStatement.executeUpdate();
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het toevoegen van de speler aan de database", e);
    }
}

@Override
public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    String sql = "SELECT tennisvlaanderenid, naam, punten FROM speler WHERE tennisvlaanderenid = ?";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, tennisvlaanderenId);
        try (var resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                int id = resultSet.getInt("tennisvlaanderenid");
                String naam = resultSet.getString("naam");
                int punten = resultSet.getInt("punten");
                return new Speler(id, naam, punten);
            } else {
                throw new InvalidSpelerException(String.valueOf(tennisvlaanderenId));
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het ophalen van de speler met ID: " + tennisvlaanderenId, e);
    }
}

@Override
public List<Speler> getAllSpelers() {
    String sql = "SELECT tennisvlaanderenid, naam, punten FROM speler";
    List<Speler> spelers = new ArrayList<>();

    try (var preparedStatement = connection.prepareStatement(sql);
         var resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
            int id = resultSet.getInt("tennisvlaanderenid");
            String naam = resultSet.getString("naam");
            int punten = resultSet.getInt("punten");
            spelers.add(new Speler(id, naam, punten));
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het ophalen van alle spelers", e);
    }

    return spelers;
}

@Override
public void updateSpelerInDb(Speler speler) {
    if (speler == null) {
        throw new IllegalArgumentException("Speler mag niet null zijn");
    }

    String sql = "UPDATE speler SET naam = ?, punten = ? WHERE tennisvlaanderenid = ?";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, speler.getNaam());
        preparedStatement.setInt(2, speler.getPunten());
        preparedStatement.setInt(3, speler.getTennisvlaanderenid());

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated == 0) {
            throw new InvalidSpelerException(String.valueOf(speler.getTennisvlaanderenid()));
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het updaten van de speler in de database", e);
    }
}

@Override
public void deleteSpelerInDb(int tennisvlaanderenid) {
    String sql = "DELETE FROM speler WHERE tennisvlaanderenid = ?";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, tennisvlaanderenid);

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted == 0) {
            throw new InvalidSpelerException(String.valueOf(tennisvlaanderenid));
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het verwijderen van de speler met ID: " + tennisvlaanderenid, e);
    }
}
@Override
public String getHoogsteRankingVanSpeler(int tennisvlaanderenid) {
    String sql = """
        SELECT t.clubnaam, w.finale, w.winnaar
        FROM wedstrijd w
        JOIN tornooi t ON w.tornooi = t.id
        WHERE (w.speler1 = ? OR w.speler2 = ? OR w.winnaar = ?)
        ORDER BY 
            CASE 
                WHEN w.finale = 1 THEN 1
                WHEN w.finale = 2 THEN 2
                ELSE 3
            END
        LIMIT 1
    """;

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, tennisvlaanderenid);
        preparedStatement.setInt(2, tennisvlaanderenid);
        preparedStatement.setInt(3, tennisvlaanderenid);

        try (var resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                String clubnaam = resultSet.getString("clubnaam");
                int finale = resultSet.getInt("finale");
                int winnaar = resultSet.getInt("winnaar");

                String plaats = switch (finale) {
                    case 1 -> (winnaar == tennisvlaanderenid) ? "de winst" : "de finale";
                    case 2 -> "de halve finale";
                    default -> "de kwartfinale of lager";
                };

                return "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in " + plaats;
            } else {
                throw new InvalidSpelerException(String.valueOf(tennisvlaanderenid));
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het ophalen van de hoogste ranking van de speler met ID: " + tennisvlaanderenid, e);
    }
}

@Override
public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    String sql = "INSERT INTO speler_speelt_tornooi (speler, tornooi) VALUES (?, ?)";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, tennisvlaanderenId);
        preparedStatement.setInt(2, tornooiId);
        preparedStatement.executeUpdate();
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het toevoegen van de speler aan het tornooi", e);
    }
}
@Override
public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    String sql = "DELETE FROM speler_speelt_tornooi WHERE speler = ? AND tornooi = ?";

    try (var preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setInt(1, tennisvlaanderenId);
        preparedStatement.setInt(2, tornooiId);

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted == 0) {
            throw new InvalidSpelerException("Geen speler gevonden met ID: " + tennisvlaanderenId + " in tornooi: " + tornooiId);
        }
    } catch (Exception e) {
        throw new RuntimeException("Fout bij het verwijderen van de speler uit het tornooi", e);
    }
}
}
