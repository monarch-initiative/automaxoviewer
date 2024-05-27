package org.monarchinitiative.automaxoviewer.controller.widgets;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.automaxoviewer.model.AutoMaxoRow;
import org.monarchinitiative.automaxoviewer.model.ItemStatus;

public class ImageTableCell<T> extends TableCell<T, AutoMaxoRow> {
    private final ImageView imageView;
    private final Image succesImage;
    private final Image failImage;

    public ImageTableCell(Image annotated, Image cannotAnnotate) {
        imageView = new ImageView();
        setGraphic(imageView);
        this.succesImage = annotated;
        this.failImage = cannotAnnotate;
    }

    @Override
    protected void updateItem(AutoMaxoRow item, boolean empty) {
        super.updateItem(item, empty);
        ItemStatus itemStatus = item.getItemStatus();
        switch (itemStatus) {
            case ANNOTATED -> imageView.setImage(succesImage);
            case CANNOT_ANNOTATE -> imageView.setImage(failImage);
            default -> imageView.setImage(null);
        }
    }
}
