package scalinghealthissues.util;

import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;

public interface IAbstractDamageablePart_NonLethalMixin {

    static void setNonLethal(AbstractDamageablePart part, boolean nonLethal) {
        if(part.canCauseDeath && part instanceof IAbstractDamageablePart_NonLethalMixin)
            ((IAbstractDamageablePart_NonLethalMixin) part).scalingHealthIssues$setNonLethal(nonLethal);
    }

    static boolean isNonLethal(AbstractDamageablePart part) {
        if(part.canCauseDeath && part instanceof IAbstractDamageablePart_NonLethalMixin) {
            return ((IAbstractDamageablePart_NonLethalMixin) part).scalingHealthIssues$isNonLethal();
        }
        return false;
    }

    static void setIgnoreRemainder(AbstractDamageablePart part, boolean ignore) {
        if(part instanceof IAbstractDamageablePart_NonLethalMixin)
            ((IAbstractDamageablePart_NonLethalMixin) part).scalingHealthIssues$setIgnoreRemainder(ignore);
    }

    static boolean shouldIgnoreRemainder(AbstractDamageablePart part) {
        if(part instanceof IAbstractDamageablePart_NonLethalMixin) {
            return ((IAbstractDamageablePart_NonLethalMixin) part).scalingHealthIssues$shouldIgnoreRemainder();
        }
        return false;
    }

    void scalingHealthIssues$setNonLethal(boolean nonLethal);
    boolean scalingHealthIssues$isNonLethal();

    void scalingHealthIssues$setIgnoreRemainder(boolean ignore);
    boolean scalingHealthIssues$shouldIgnoreRemainder();
}
