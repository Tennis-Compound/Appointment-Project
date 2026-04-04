package sprent2;

public class ParticipantRule {
    private int maxParticipants;

    public ParticipantRule(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    // الطريقة الصحيحة: تمرر عدد المشاركين كـ int
    public boolean isValid(int participants) {
        return participants <= maxParticipants;
    }

    // الطريقة الصحيحة لاستدعاء الحد الأقصى
    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}