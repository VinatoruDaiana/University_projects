package View;

public class Feedback {
    private int feedbackId;
    private int hotelId;
    private String comment;
    private int rating;

    // Constructor
    public Feedback(int feedbackId, int hotelId, String comment, int rating) {
        this.feedbackId = feedbackId;
        this.hotelId = hotelId;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters și Setters pentru atributele clasei Feedback
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Clientul cu feedback-ul " + feedbackId +
                " a lăsat un comentariu " + comment +
                " și un rating legat de curatenie de " + rating +
                " pentru hotelul cu ID-ul " + hotelId;
    }
}
