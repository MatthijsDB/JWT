// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package jwt.actions;

import java.io.UnsupportedEncodingException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.DataValidationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import jwt.helpers.AlgorithmParser;
import jwt.helpers.AudienceListToStringArrayConverter;
import jwt.helpers.DecodedJWTParser;
import jwt.proxies.constants.Constants;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Decodes a JWT string into a JWT object. Throws an exception when the token could not be decoded.
 */
public class DecodeJWT extends CustomJavaAction<IMendixObject>
{
	private java.lang.String token;
	private java.lang.String secret;
	private jwt.proxies.ENU_Algorithm algorithm;
	private IMendixObject __claimsToVerify;
	private jwt.proxies.JWT claimsToVerify;

	public DecodeJWT(IContext context, java.lang.String token, java.lang.String secret, java.lang.String algorithm, IMendixObject claimsToVerify)
	{
		super(context);
		this.token = token;
		this.secret = secret;
		this.algorithm = algorithm == null ? null : jwt.proxies.ENU_Algorithm.valueOf(algorithm);
		this.__claimsToVerify = claimsToVerify;
	}

	@Override
	public IMendixObject executeAction() throws Exception
	{
		this.claimsToVerify = __claimsToVerify == null ? null : jwt.proxies.JWT.initialize(getContext(), __claimsToVerify);

		// BEGIN USER CODE
		ILogNode logger = Core.getLogger(Constants.getLOGNODE());
		
		if (this.token == null || this.token.equals("")) {
			logger.error("Cannot decode an empty token.");
			throw new DataValidationRuntimeException("Cannot decode an empty token.");
		}
		
		if (this.secret == null || this.secret.equals("")) {
			logger.error("Cannot decode token using an empty secret.");
			throw new DataValidationRuntimeException("Cannot decode token using an empty secret.");
		}
		
		if (this.algorithm == null) {
			logger.error("Cannot decode token using an empty algorithm.");
			throw new DataValidationRuntimeException("Cannot decode token using an empty algorithm.");
		}
		
		DecodedJWT jwt = null;
		
		try {
			Algorithm alg = new AlgorithmParser().parseAlgorithm(algorithm, secret);
			logger.debug("Starting to decode JWT token with algorithm " + alg.getName() + ".");
			
			Verification verification = JWT.require(alg);
			
			if (claimsToVerify != null) {
				if (claimsToVerify.getiss() != null) {
					logger.debug("Verify issuer with value: " + claimsToVerify.getiss() + ".");
					verification.withIssuer(claimsToVerify.getiss());
				}
			
				if (claimsToVerify.getjti() != null) {
					logger.debug("Verify JWT token ID with value: " + claimsToVerify.getjti() + ".");
					verification.withJWTId(claimsToVerify.getjti());
				}
				
				if (claimsToVerify.getsub() != null) {
					logger.debug("Verify subject with value: " + claimsToVerify.getsub() + ".");
					verification.withSubject(claimsToVerify.getsub());
				}
				
				String[] audienceList = new AudienceListToStringArrayConverter().convert(this.context(), claimsToVerify);
				
				if (audienceList.length > 0) {
					logger.debug("Verify with list of " + audienceList.length + " audiences.");
					verification.withAudience(audienceList);
				}
			}
			
			JWTVerifier verifier = verification.build();
			jwt = verifier.verify(token);
			
			logger.debug("Verifying token successfull.");
		} catch (UnsupportedEncodingException exception){
		    logger.error("Token encoding unsupported.", exception);
		    throw exception;
		} catch (JWTVerificationException exception){
			logger.error("Verification of token signature/claims failed.", exception);
			throw exception;
		} 
		
		 
		 return new DecodedJWTParser()
				 .parse(this.context(), logger, jwt)
				 .getMendixObject();
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "DecodeJWT";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
