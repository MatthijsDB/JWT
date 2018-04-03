// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package jwt.actions;

import java.util.Iterator;
import java.util.List;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.DataValidationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import jwt.helpers.AlgorithmParser;
import jwt.helpers.AudienceListToStringArrayConverter;
import jwt.helpers.RegisteredClaimIdentifier;
import jwt.proxies.PublicClaim;
import jwt.proxies.PublicClaimBoolean;
import jwt.proxies.PublicClaimDate;
import jwt.proxies.PublicClaimDecimal;
import jwt.proxies.PublicClaimInteger;
import jwt.proxies.PublicClaimLong;
import jwt.proxies.PublicClaimString;
import jwt.proxies.constants.Constants;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Generates a JWT token string from a JWT object. Make sure all inputs are specified correctly. Token generation without specifying a secret, is not allowed.
 */
public class GenerateJWT extends CustomJavaAction<java.lang.String>
{
	private IMendixObject __jwtObject;
	private jwt.proxies.JWT jwtObject;
	private java.lang.String secret;
	private jwt.proxies.ENU_Algorithm algorithm;

	public GenerateJWT(IContext context, IMendixObject jwtObject, java.lang.String secret, java.lang.String algorithm)
	{
		super(context);
		this.__jwtObject = jwtObject;
		this.secret = secret;
		this.algorithm = algorithm == null ? null : jwt.proxies.ENU_Algorithm.valueOf(algorithm);
	}

	@Override
	public java.lang.String executeAction() throws Exception
	{
		this.jwtObject = __jwtObject == null ? null : jwt.proxies.JWT.initialize(getContext(), __jwtObject);

		// BEGIN USER CODE
		ILogNode logger = Core.getLogger(Constants.getLOGNODE());
		
		if (jwtObject == null) {
			logger.error("Input JWT object for Generate JWT is empty.");
			throw new DataValidationRuntimeException("Input JWT object for Generate JWT is empty.");
		}
		
		if (algorithm == null) {
			logger.error("Input algorithm for Generate JWT is empty.");
			throw new DataValidationRuntimeException("Input algorithm for Generate JWT is empty.");
		}
		
		Algorithm alg = new AlgorithmParser().parseAlgorithm(algorithm, secret);
		logger.debug("Starting to gerenate JWT token with algorithm " + alg.getName() + ".");
		
		Builder builder = JWT.create()
		        .withIssuer(jwtObject.getiss())
		        .withExpiresAt(jwtObject.getexp())
		        .withSubject(jwtObject.getsub())
		        .withJWTId(jwtObject.getjti())
		        .withNotBefore(jwtObject.getnbf())
		        .withIssuedAt(jwtObject.getiat());
		
		String[] audienceList = new AudienceListToStringArrayConverter().convert(this.context(), jwtObject);
		logger.debug("Adding audience claim with " + audienceList.length + " audiences.");
		builder.withAudience(audienceList);
		
		List<IMendixObject> claims = Core.retrieveByPath(this.context(), jwtObject.getMendixObject(), "JWT.Claim_JWT");
		logger.debug("Adding " + claims.size() + " public claims.");
		
		Iterator<IMendixObject> claimIterator = claims.iterator();
		
		while(claimIterator.hasNext()) {
			IMendixObject claimObject = claimIterator.next();
			PublicClaim claim = PublicClaim.initialize(this.context(), claimObject);
			
			if (claim.getClaim() == null) {
				logger.error("Empty public claim found in JWT input object.");
				throw new DataValidationRuntimeException("Empty public claim found in JWT input object.");
			}
			
			RegisteredClaimIdentifier registeredClaimIdentifier = new RegisteredClaimIdentifier();
			
			if (registeredClaimIdentifier.identify(claim.getClaim())) {
				logger.warn("Registered claim " + claim.getClaim() + " found in Public Claims. Claim will be skipped.");
				continue;
			}
			
			logger.debug("Adding claim " + claim.getClaim() + " of entity " + claim.getClass().getSimpleName() + ".");
			
			if (claim.getClass() == PublicClaimBoolean.class) {
				PublicClaimBoolean claimBoolean = PublicClaimBoolean.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimBoolean.getClaim(), claimBoolean.getValue());
			} else if (claim.getClass() == PublicClaimDate.class) {
				PublicClaimDate claimDate = PublicClaimDate.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimDate.getClaim(), claimDate.getValue());
			} else if (claim.getClass() == PublicClaimInteger.class) {
				PublicClaimInteger claimInteger = PublicClaimInteger.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimInteger.getClaim(), claimInteger.getValue());
			} else if (claim.getClass() == PublicClaimLong.class) {
				PublicClaimLong claimLong = PublicClaimLong.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimLong.getClaim(), claimLong.getValue());
			} else if (claim.getClass() == PublicClaimDecimal.class) {
				PublicClaimDecimal claimDecimal = PublicClaimDecimal.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimDecimal.getClaim(), claimDecimal.getValue().doubleValue());
			} else if (claim.getClass() == PublicClaimString.class) {
				PublicClaimString claimString = PublicClaimString.initialize(this.context(), claim.getMendixObject());
				builder.withClaim(claimString.getClaim(), claimString.getValue());
			} else {
				logger.warn("Incorrect specialization of PublicClaim detected for claim " + claim.getClaim() + ".");
			}			
		}
		
		String token = builder.sign(alg);
		
		logger.debug("Token successfully generated.");
		
		return token;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "GenerateJWT";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
