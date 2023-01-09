package com.example.studybuddy;

public interface RecyclerViewInterface {
//    void onWhatsAppMessageClick(int position);
//    void onCancelClassClick(int position);

    void onCancelClassClick(String name, String subject, String date);
    void onWhatsAppMessageClick(String name, String subject, String date);
    void onPayForClassClick(String name, String subject, String date);
    void onApprovePaymentForClassClick(String name, String subject, String date);
}
