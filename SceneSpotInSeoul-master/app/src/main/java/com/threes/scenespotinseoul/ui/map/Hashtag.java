package com.threes.scenespotinseoul.ui.map;

import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import com.threes.scenespotinseoul.R;

public class Hashtag extends ClickableSpan {

  private ClickEventListener mClickEventListener = null;
  private Context context;

  public Hashtag(Context ctx) {
    super();
    context = ctx;
  }

  public void setOnClickEventListener(ClickEventListener listener) {
    mClickEventListener = listener;
  }

  @Override
  public void updateDrawState(TextPaint ds) {
    ds.setColor(context.getColor(R.color.colorAccent));
  }

  @Override
  public void onClick(View widget) {
    TextView tv = (TextView) widget;
    Spanned s = (Spanned) tv.getText();
    int start = s.getSpanStart(this);
    int end = s.getSpanEnd(this);
    String theWord = s.subSequence(start + 1, end).toString();
    mClickEventListener.onClickEvent(theWord);
  }

  public interface ClickEventListener {

    void onClickEvent(String data);

  }
}

