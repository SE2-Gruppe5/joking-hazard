package at.derfl007.jokinghazard.util;

public class ErrorMessages {

    public static String convertErrorMessages(String errorCode) {

        String converted = "An Error occured";

        switch(errorCode) {

            case "already_in_room":
                converted = "You are already in a romm";
                break;

            case "no_free_room":
                converted = "No Room available";
                break;

            case "room_doesnt_exist":
                converted = "Wrong Room Code";
                break;

            case "room_full":
                converted = "The Room is full";
                break;

            case "room_closed":
                converted = "Admin left the room";
                break;

            case "user_left_game":
                converted = "left room";
                break;

            case "user_became_admin":
                converted = "became admin";
                break;

            case "user_not_an_admin":
                converted = "You're not an admin";
                break;

            case "user_not_in_a_room":
                converted = "This USer is not in a room";
                break;

            case "user_doesnt_exist":
                break;

            case "card_doesnt_exist":
                converted = "Card does not exist";
                break;

            case "pile_doesnt_exist":
                converted = "Pile does not exist";
                break;

            case "invalid_pile_index":
                converted = "Invalid Pile index";
                break;
        }

        return converted;
    }
}
