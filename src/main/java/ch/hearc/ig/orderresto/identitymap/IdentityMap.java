package ch.hearc.ig.orderresto.identitymap;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap<ID, T> {

    private final Map<ID, T> cacheById = new HashMap<>();
    private final Map<String, T> cacheByEmail = new HashMap<>();

    // Récupérer une entité par ID
    public T getById(ID id) {
        return cacheById.get(id);
    }

    // Récupérer une entité par email
    public T getByEmail(String email) {
        return cacheByEmail.get(email);
    }

    // Ajouter une entité au cache
    public void put(ID id, String email, T entity) {
        cacheById.put(id, entity);
        cacheByEmail.put(email, entity);
        System.out.println("Client ajouté dans le cache : ID = " + id + ", email = " + email);
    }


    // Vérifier si une entité est dans le cache par ID
    public boolean containsById(ID id) {
        return cacheById.containsKey(id);
    }

    // Vérifier si une entité est dans le cache par email
    public boolean containsByEmail(String email) {
        return cacheByEmail.containsKey(email);
    }

    // Supprimer une entité du cache
    public void removeById(ID id) {
        T entity = cacheById.remove(id);
        if (entity != null) {
            cacheByEmail.values().remove(entity);
        }
    }

    // Supprimer une entité du cache par email
    public void removeByEmail(String email) {
        T entity = cacheByEmail.remove(email);
        if (entity != null) {
            cacheById.values().remove(entity);
        }
    }

    // Méthode pour imprimer le contenu du cache
    public void printCache() {
        System.out.println("Contenu du cache (par ID) : " + cacheById);
        System.out.println("Contenu du cache (par email) : " + cacheByEmail);
    }
}
