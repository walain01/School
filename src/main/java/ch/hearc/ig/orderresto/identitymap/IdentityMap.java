package ch.hearc.ig.orderresto.identitymap;
import java.util.HashMap;
import java.util.Map;

public class IdentityMap<ID, T> {

    private final Map<ID, T> cache = new HashMap<>();

    // Récupérer une entité par son ID
    public T get(ID id) {
        return cache.get(id);
    }

    // Ajouter une entité au cache
    public void put(ID id, T entity) {
        cache.put(id, entity);
    }

    // Vérifier si une entité est dans le cache
    public boolean contains(ID id) {
        return cache.containsKey(id);
    }

    // Supprimer une entité du cache
    public void remove(ID id) {
        cache.remove(id);
    }

    public void printCache() {
        System.out.println("Contenu de l'Identity Map :");
        for (Map.Entry<ID, T> entry : cache.entrySet()) {
            System.out.println("ID: " + entry.getKey() + ", Objet: " + entry.getValue());
        }
    }
}
