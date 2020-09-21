package ec.com.innovatech.mobileinvoice.invoices;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import com.google.android.gms.common.util.CollectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ec.com.innovatech.mobileinvoice.models.DetailInvoice;
import ec.com.innovatech.mobileinvoice.util.ValidationUtil;

public class ReportInvoiceAdapter extends PrintDocumentAdapter {

    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 1;
    private List<DetailInvoice> detailsInvoice;

    public ReportInvoiceAdapter(Context context, List<DetailInvoice> detailsInvoice){
        this.context = context;
        this.detailsInvoice = detailsInvoice;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        myPdfDocument = new PrintedPdfDocument(context, newAttributes);
        pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
        pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;

        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pages, i)){
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create();
                PdfDocument.Page page = myPdfDocument.startPage(newPage);
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                drawPage(page);
                myPdfDocument.finishPage(page);
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pages);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page){
        for (int i = 0; i<pageRanges.length; i++){
            if ((page >= pageRanges[i].getStart()) && (page <= pageRanges[i].getEnd())) {
                return true;
            }
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();

        int titleBaseLine = 52;
        int leftMargin = 150;
        double totalSales = 0;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText("Reporte de ventas", leftMargin, titleBaseLine, paint);
        paint.setTextSize(20);
        leftMargin = 25;
        titleBaseLine = titleBaseLine + 25;
        canvas.drawText(ValidationUtil.completeSpaceTwoWay(65,"", "_"), leftMargin, titleBaseLine, paint);
        titleBaseLine = titleBaseLine + 35;
        canvas.drawText(ValidationUtil.completeSpaceString(43,"Artículo")+""+
                        ValidationUtil.completeSpaceTwoWay(10,"U.Man", " ")+""+
                        ValidationUtil.completeSpaceTwoWay(10,"Cant.", " ")+""+
                        ValidationUtil.completeSpaceTwoWay(12,"P.Unit.", " ")+""+
                        ValidationUtil.completeSpaceTwoWay(12,"V.Total", " "), leftMargin, titleBaseLine, paint);
        titleBaseLine = titleBaseLine + 25;
        canvas.drawText(ValidationUtil.completeSpaceTwoWay(63,"", "_"), leftMargin, titleBaseLine, paint);
        if(!CollectionUtils.isEmpty(detailsInvoice)) {
            for (DetailInvoice det : detailsInvoice) {
                titleBaseLine = titleBaseLine + 25;
                String unitFormat = ValidationUtil.getTwoDecimal(Double.valueOf(det.getUnitValue()));
                String totalFormat = ValidationUtil.getTwoDecimal(Double.valueOf(det.getSubTotal()));
                totalSales = totalSales + Double.valueOf(det.getSubTotal());
                canvas.drawText(ValidationUtil.completeSpaceString(25, det.getDescription()), leftMargin, titleBaseLine, paint);
                canvas.drawText(ValidationUtil.completeSpaceString(10, det.getValueCatalogDriverUnit()+"x"+det.getValueDriverUnit()), 280, titleBaseLine, paint);
                canvas.drawText(ValidationUtil.completeSpaceString(12, det.getQuantity()), 370, titleBaseLine, paint);
                int restMargin = unitFormat.length() - 4;
                int valMargin = 403 - restMargin * 7;
                canvas.drawText( ValidationUtil.completeSpaceNumber(12, unitFormat) , valMargin, titleBaseLine, paint);
                restMargin = totalFormat.length() - 5;
                valMargin = 488 - restMargin * 7;
                canvas.drawText(ValidationUtil.completeSpaceNumber(12, totalFormat), valMargin, titleBaseLine, paint);
            }
        }
        titleBaseLine = titleBaseLine + 20;
        canvas.drawText(ValidationUtil.completeSpaceTwoWay(63,"", "_"), leftMargin, titleBaseLine, paint);
        titleBaseLine = titleBaseLine + 25;
        String totalReportFormat = ValidationUtil.getTwoDecimal(totalSales);
        int restMargin = totalReportFormat.length() - 5;
        int valMargin = 488 - restMargin * 7;
        canvas.drawText(ValidationUtil.completeSpaceNumber(12, totalReportFormat), valMargin, titleBaseLine, paint);
        titleBaseLine = titleBaseLine + 15;
        canvas.drawText(ValidationUtil.completeSpaceTwoWay(63,"", "_"), leftMargin, titleBaseLine, paint);
    }
}
