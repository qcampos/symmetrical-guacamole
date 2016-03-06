package fr.upem.hireanemployee.profildata;

import java.io.Serializable;

/**
 * Primary key of the joint between entities Employee and Skill (AKA Skill).
 */
public class SkillAssociationId implements Serializable {

    private long employeeId;
    private long skillId;

    @Override
    public int hashCode() {
        return (int) (employeeId + skillId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SkillAssociationId)) {
            return false;
        }
        SkillAssociationId otherId = (SkillAssociationId) obj;
        return otherId.employeeId == employeeId && otherId.skillId == skillId;
    }
}
