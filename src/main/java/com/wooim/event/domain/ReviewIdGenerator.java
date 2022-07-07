package com.wooim.event.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

public class ReviewIdGenerator extends IdentityGenerator {
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		if (object instanceof Review) {
			Review p = (Review) object;
			return p.getReviewId() == null ? super.generate(session, object) : p.getReviewId();
		} else {
			throw new RuntimeException("Review entity가 아니에요.");
		}
	}
}