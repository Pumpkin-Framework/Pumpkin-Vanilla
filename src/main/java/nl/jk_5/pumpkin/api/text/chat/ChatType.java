package nl.jk_5.pumpkin.api.text.chat;

import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.annotation.CatalogedBy;

/**
 * Represents the type of chat a message can be sent to.
 *
 * @see ChatTypes
 */
@CatalogedBy(ChatTypes.class)
public interface ChatType extends CatalogType {

}
