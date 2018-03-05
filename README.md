# PToast
自定义toast
功能：
1.设置时间
2.设置位置
3.是否直接替换

PToast.makeText(this, msg)
      .duration(PToast.Time.LENGTH_LONG) //持续时间
      .gravity(PToast.Gravity.BOTTOM) //位置
      .replace(false) //是否直接替换
      .show()
