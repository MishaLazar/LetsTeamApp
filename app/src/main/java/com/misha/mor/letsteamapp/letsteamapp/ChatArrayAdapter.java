package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Context;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Misha on 9/11/2016.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {


    String userID;
    TextView chatText;
    List<ChatMessage> chatMessageList = new ArrayList<>();
    Context context;
    ChatMessage currentMSG;

    @Override
    public void add(ChatMessage object) {
        this.currentMSG = object;
        chatMessageList.add(currentMSG);
        Collections.sort(chatMessageList,Comparators.MessageOrder2);
        super.add(currentMSG);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId ,String userID) {
        super(context, textViewResourceId);
        this.context = context;
        this.userID = userID;

    }
    public void clearListAdapter(){
        chatMessageList.clear();
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);
        int myMsg = checkIsMe(chatMessage.getUserId());

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //for side selections


        try {
            setAlignment(holder, myMsg);
        }catch (Exception exc){
            Log.d("chatAdapter",exc.getMessage());
            exc.getStackTrace();
        }

        holder.txtMessage.setText(chatMessage.getMessage() );
        holder.txtMessageDate.setText(" "+chatMessage.getTimeOnly());
        holder.txtInfo.setText(chatMessage.getOwnerName());
        holder.messageOwnerProfilePic.setImageBitmap(
                ImageConverter.getRoundedCornerBitmap(ImageConverter.StringToBitMap(
                        chatMessage.getBitmapStringUserPic()),40));

        return convertView;
    }

    private int checkIsMe(String userId) {
        if(userId.equals(this.userID))
            return -1;
        else
            return 1;
    }

    private void setAlignment(ViewHolder holder,int isMe) {
        if (isMe == 1) {

            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);
            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            View vImg = holder.content.getChildAt(1);
            View vBubble = holder.content.getChildAt(0);
            if (!(vImg instanceof ImageView )){
                holder.content.removeViewAt(1);
                holder.content.removeViewAt(0);

                holder.content.addView(vImg);
                holder.content.addView(vBubble);
            }

        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);


            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            /*lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);*/
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
            /*holder.messageOwnerProfilePic.setLayoutParams(layoutParams);*/

            View vImg = holder.content.getChildAt(0);
            View vBubble = holder.content.getChildAt(1);
            if (!(vImg instanceof ImageView )){
                holder.content.removeViewAt(1);
                holder.content.removeViewAt(0);

                holder.content.addView(vBubble);
                holder.content.addView(vImg);

            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.txtMessageDate = (TextView) v.findViewById(R.id.txtMessageDate);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.messageOwnerProfilePic = (ImageView)v.findViewById(R.id.imgProfilePic);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtMessageDate;
        public TextView txtInfo;
        public ImageView messageOwnerProfilePic;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}
