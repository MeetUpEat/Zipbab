import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @param density resources.displayMetrics.density를 전달해주세요.
 *
 * */
class ListItemDecoration(
    private val density: Float
) : ItemDecoration() {

    private var topPaddingValues: Int = 0
    private var startPaddingValues: Int = 0
    private var endPaddingValues: Int = 0
    private var bottomPaddingValues: Int = 0

    /**
     * @param topDp 원하는 top padding dp 값을 숫자만 입력하면 됩니다.
     * @param bottomDp 원하는 top padding dp 값을 숫자만 입력하면 됩니다.
     * @param startDp 원하는 top padding dp 값을 숫자만 입력하면 됩니다.
     * @param endDp 원하는 top padding dp 값을 숫자만 입력하면 됩니다.
     * */
    fun setPaddingValues(
        topDp: Int = 0,
        bottomDp: Int = 0,
        startDp: Int = 0,
        endDp: Int = 0,
    ) {
        topPaddingValues = (topDp * density + 0.5f).toInt()
        startPaddingValues = (startDp * density + 0.5f).toInt()
        endPaddingValues = (endDp * density + 0.5f).toInt()
        bottomPaddingValues = (bottomDp * density + 0.5f).toInt()
    }

    /**
     * @param dp 원하는 pdding dp 값을 숫자만 입력하면 됩니다. top, bottom, start, end 모두 해당 값으로 정합니다.
     * */
    fun setPaddingValues(
        dp: Int = 0
    ) {
        val temp = (dp * density + 0.5f).toInt()

        topPaddingValues = temp
        startPaddingValues = temp
        endPaddingValues = temp
        bottomPaddingValues = temp
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        with(outRect) {
            top = topPaddingValues
            bottom = bottomPaddingValues
            left = startPaddingValues
            right = endPaddingValues
        }
    }
}