package com.metrosix.noteasaurus.domain.util;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Person;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Query;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class CorkboardUtility {
    static private final Pattern uniqifierPattern = Pattern.compile("-\\d+$");
    static private final int MAX_LABEL_SIZE = 128;

    private PersistenceManager persistenceManager;

    public CorkboardUtility(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    public void removeFocusFor(Person person) {
        Query q = getPersistenceManager().getSession().createQuery(
            "update Corkboard set focused = :newFocused where focused = :oldFocused and owner = :owner"
                );
        q.setParameter("newFocused", Boolean.FALSE);
        q.setParameter("oldFocused", Boolean.TRUE);
        q.setParameter("owner", person);
        q.executeUpdate();
    }

    public String getUniqueLabel(String label, Person owner) {
        return getUniqueLabel(label, owner, null);
    }

    public Short getDefaultWeight(Short weight, Person owner) {
        // validate our parameters.
        if (owner == null) {
            throw new IllegalStateException("The parameter owner must be non-null.");
        }

        if (weight != null) {
            return weight;
        }

        Query q = getPersistenceManager().getSession().createQuery(
                "select max(weight) from Corkboard where owner = :owner");
        q.setParameter("owner", owner);
        Number maxWeight = (Number)q.uniqueResult();
        if (maxWeight != null) {
            return Short.valueOf((short)(maxWeight.shortValue() + (short)1));
        } else {
            return Short.valueOf((short)0);
        }
    }

    public String getUniqueLabel(String label, Person owner, Corkboard corkboard) {
        // validate our parameters.
        if (owner == null) {
            throw new IllegalStateException("The parameter owner must be non-null.");
        }

        // cleanup the label, ensure it is not null, trim whitespace and is short enough for the field.
        label = cleanupLabel(label);

        // See if any other labels exist for the user.
        if (!isLabelUnique(label, owner, corkboard)) {
            // we have a duplicate label.

            // Strip off any uniqifier "-1" or similar.
            label = stripUniqifier(label);

            // Get all of the currently used labels.
            Set<String> takenLabels = getUsedLabels(owner, corkboard);

            String fullLabel;
            String baseLabel = label;
            int iteration = 0;
            
            do {
                iteration++;
                String uniqifier = "-" + iteration;
                if (baseLabel.length() + uniqifier.length() > MAX_LABEL_SIZE) {
                    baseLabel = baseLabel.substring(0, MAX_LABEL_SIZE - uniqifier.length());
                }
                fullLabel = baseLabel + uniqifier;
            } while(takenLabels.contains(fullLabel));
            return fullLabel;
        }

        return label;
    }

    protected String stripUniqifier(String label) {
        Matcher m = uniqifierPattern.matcher(label);
        if (m.matches()) {
            label = label.substring(0, label.lastIndexOf("-"));
        }
        return label;
    }
    
    protected String cleanupLabel(String label) {
        if (label == null || label.trim().length() == 0) {
            label = "Default Corkboard";
        }
        label = label.trim();

        if (label.length() > MAX_LABEL_SIZE) {
            label = label.substring(0, MAX_LABEL_SIZE);
        }
        return label;
    }

    protected boolean isLabelUnique(String label, Person owner, Corkboard corkboard) {
        if (label == null) {
            throw new IllegalArgumentException("The parameter label must be non-null.");
        }
        if (owner == null) {
            throw new IllegalArgumentException("The parameter owner must be non-null.");
        }

        String hql = "select count(c) from Corkboard c where c.label = :label and c.owner = :owner ";
        if (corkboard != null && corkboard.getId() != 0) {
            hql += " and c.id != :id";
        }

        Query q = getPersistenceManager().getSession().createQuery(hql);
        q.setParameter("label", label);
        q.setParameter("owner", owner);
        if (corkboard != null && corkboard.getId() != 0) {
            q.setParameter("id", corkboard.getId());
        }

        Number count = (Number)q.uniqueResult();
        return count.intValue() == 0;
    }

    protected Set<String> getUsedLabels(Person owner, Corkboard corkboard) {
        if (owner == null) {
            throw new IllegalArgumentException("The parameter owner must be non-null.");
        }

        Set<String> result = new HashSet<String>();
        String hql = "select c.label from Corkboard c where c.owner = :owner ";
        if (corkboard != null && corkboard.getId() != 0) {
            hql += " and c.id != :id";
        }
        Query q = getPersistenceManager().getSession().createQuery(hql);
        q.setParameter("owner", owner);
        if (corkboard != null && corkboard.getId() != 0) {
            q.setParameter("id", corkboard.getId());
        }
        result.addAll(q.list());
        return result;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
