package com.example.coyotefree;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatbotBottomSheet extends BottomSheetDialogFragment {

    private static String lastResponse = "AI will respond here";

    private EditText inputMessage;
    private Button sendBtn;
    private TextView responseView;
    private ScrollView scrollContainer;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.requestLayout();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setDraggable(false);
                behavior.setHideable(false);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatbot_bottom_sheet, container, false);

        inputMessage = view.findViewById(R.id.inputMessage);
        sendBtn = view.findViewById(R.id.sendBtn);
        responseView = view.findViewById(R.id.responseView);
        responseView.setLineSpacing(12f, 1.2f);

        scrollContainer = view.findViewById(R.id.scroll_container);

        try {
            JSONObject jsonResponse = new JSONObject(lastResponse);
            String responseText = jsonResponse.getString("response");
            responseView.setText(responseText);
        } catch (Exception e) {
            responseView.setText(lastResponse);
        }

        sendBtn.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputMessage.getWindowToken(), 0);

                inputMessage.setText("");
                new ChatRequestTask(msg).execute();
            }
        });

        return view;
    }

    private class ChatRequestTask extends android.os.AsyncTask<Void, Void, String> {
        private final String userMessage;

        ChatRequestTask(String msg) {
            this.userMessage = msg;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://groq-server-0sip.onrender.com/chat");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("message", userMessage);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                conn.disconnect();

                return sb.toString();
            } catch (Exception e) {
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            lastResponse = result;

            try {
                JSONObject jsonResponse = new JSONObject(result);
                String responseText = jsonResponse.getString("response");

                String cleanText = stripMarkdown(responseText);
                String beautifiedText = addLineBreaks(cleanText, 5);
                responseView.setText(beautifiedText);
            } catch (Exception e) {
                responseView.setText(result);
            }

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputMessage.getWindowToken(), 0);

            scrollContainer.post(() -> scrollContainer.fullScroll(View.FOCUS_DOWN));
        }

        private String stripMarkdown(String text) {
            return text
                    .replaceAll("\\*\\*(.*?)\\*\\*", "$1")  // Remove **bold**
                    .replaceAll("\\*(.*?)\\*", "$1")        // Remove *italic*
                    .replaceAll("_(.*?)_", "$1")            // Remove _italic_
                    .replaceAll("`(.*?)`", "$1");           // Remove `code`
        }

        private String addLineBreaks(String text, int groupSize) {
            text = text.replaceAll("\n{3,}", "\n\n"); // Avoid triple newlines

            String[] lines = text.split("\n");
            StringBuilder result = new StringBuilder();
            int count = 0;

            for (String line : lines) {
                result.append(line).append("\n");

                if (
                        line.trim().startsWith("*") ||
                                line.trim().startsWith("-") ||
                                line.contains("**") ||
                                line.contains("*")
                ) {
                    count++;
                } else if (!line.trim().isEmpty()) {
                    count++;
                }

                if (count > 0 && count % groupSize == 0) {
                    result.append("\n");
                }
            }

            return result.toString().trim();
        }
    }
}
