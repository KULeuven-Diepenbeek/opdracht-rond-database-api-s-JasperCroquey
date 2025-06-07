package be.kuleuven;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


public class SpelerRepositoryJPAimpl implements SpelerRepository {
  private final EntityManager em;
  public static final String PERSISTANCE_UNIT_NAME = "be.kuleuven.spelerhibernateTest";

  // Constructor
  public SpelerRepositoryJPAimpl(EntityManager entityManager) {
    if (entityManager == null) {
      throw new IllegalArgumentException("EntityManager mag niet null zijn");
    }
    this.em = entityManager;
  }

@Override
public void addSpelerToDb(Speler speler) {
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        boolean bestaat = em.find(Speler.class, speler.getTennisvlaanderenId()) != null;
        if (bestaat) {
            tx.rollback();
            throw new RuntimeException(" A PRIMARY KEY constraint failed");
        }
        em.persist(speler);
        tx.commit();
    } catch (Exception ex) {
        if (tx.isActive()) tx.rollback();
        throw ex;
    }
}

  @Override
  public Speler getSpelerByTennisvlaanderenId(int tennisvlaanderenId) {
    Speler speler = em.find(Speler.class, tennisvlaanderenId);
    if (speler == null) {
      throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenId);
    }
    return speler;
  }

  @Override
  public List<Speler> getAllSpelers() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Speler> cq = cb.createQuery(Speler.class);
    Root<Speler> root = cq.from(Speler.class);
    cq.select(root);
    return em.createQuery(cq).getResultList();
  }

  @Override
  public void updateSpelerInDb(Speler speler) {
    if (speler == null) {
      throw new IllegalArgumentException("Speler mag niet null zijn");
    }

    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Speler existingSpeler = em.find(Speler.class, speler.getTennisvlaanderenId());
      if (existingSpeler == null) {
        throw new RuntimeException("Invalid Speler met identification: " + speler.getTennisvlaanderenId());
      }
      em.merge(speler);
      tx.commit();
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  @Override
  public void deleteSpelerInDb(int tennisvlaanderenId) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      Speler speler = em.find(Speler.class, tennisvlaanderenId);
      if (speler == null) {
        throw new RuntimeException("Invalid Speler met identification: " + tennisvlaanderenId);
      }
      em.remove(speler);
      tx.commit();
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  @Override
  public String getHoogsteRankingVanSpeler(int tennisvlaanderenId) {
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

    Object[] result = (Object[]) em.createNativeQuery(sql)
        .setParameter("id", tennisvlaanderenId)
        .getSingleResult();

    String clubnaam = (String) result[0];
    int finale = (int) result[1];
    int winnaar = (int) result[2];

    String plaats = switch (finale) {
      case 1 -> (winnaar == tennisvlaanderenId) ? "de winst" : "de finale";
      case 2 -> "de halve finale";
      default -> "de kwartfinale of lager";
    };

    return "Hoogst geplaatst in het tornooi van " + clubnaam + " met plaats in " + plaats;
  }

  @Override
  public void addSpelerToTornooi(int tornooiId, int tennisvlaanderenId) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      Speler speler = em.find(Speler.class, tennisvlaanderenId);
      Tornooi tornooi = em.find(Tornooi.class, tornooiId);

      if (speler == null || tornooi == null) {
        throw new IllegalArgumentException("Speler of Tornooi niet gevonden");
      }

      speler.getTornooien().add(tornooi);
      em.merge(speler);

      tx.commit();
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  @Override
  public void removeSpelerFromTornooi(int tornooiId, int tennisvlaanderenId) {
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();

      Speler speler = em.find(Speler.class, tennisvlaanderenId);
      Tornooi tornooi = em.find(Tornooi.class, tornooiId);

      if (speler == null || tornooi == null) {
        throw new IllegalArgumentException("Speler of Tornooi niet gevonden");
      }

      speler.getTornooien().remove(tornooi);
      em.merge(speler);

      tx.commit();
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }
}