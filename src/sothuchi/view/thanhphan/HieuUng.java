package sothuchi.view.thanhphan;

import java.util.function.Consumer;

import javax.swing.Timer;

// Tạo hiệu ứng: Timer 15ms, tính tiến độ t từ 0 -> 1 rồi gọi capNhat.
public final class HieuUng {

    private HieuUng() {
    }

    public static Timer chay(final int thoiGianMs, final Consumer<Float> capNhat, final Runnable khiXong) {
        final long batDau = System.currentTimeMillis();
        final Timer timer = new Timer(15, null);
        timer.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - batDau) / (float) thoiGianMs);
            // ease-out: nhanh lúc đầu, chậm dần
            float muot = 1 - (1 - t) * (1 - t) * (1 - t);
            capNhat.accept(muot);
            if (t >= 1f) {
                timer.stop();
                if (khiXong != null) {
                    khiXong.run();
                }
            }
        });
        timer.start();
        return timer;
    }
}
