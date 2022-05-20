package ru.syntez.vault.sds.exceptions;

/**
 * Wrapper over RuntimeException. Includes additional options for formatting message text.
 *
 * @author Skyhunter
 * @date 09.09.2021
 */
public class VaultException extends RuntimeException {

    public VaultException(String message) {
	    super(message);
    }
   
}
