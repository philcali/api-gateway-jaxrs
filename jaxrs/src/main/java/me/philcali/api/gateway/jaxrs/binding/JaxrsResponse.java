package me.philcali.api.gateway.jaxrs.binding;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Variant;

public class JaxrsResponse extends Response {
    final int statusCode;
    final Object entity;
    final Map<String, NewCookie> cookies;
    final MultivaluedMap<String, Object> headers;
    final Set<String> allowedMethods;
    final Date lastModified;
    final URI location;
    final Locale language;
    final MediaType mediaType;
    final String encoding;
    final Set<Link> links;

    protected JaxrsResponse(final int statusCode, final Object entity, final Map<String, NewCookie> cookies,
            final MultivaluedMap<String, Object> headers, Set<String> allowedMethods, Date lastModified, URI location,
            Locale language, MediaType mediaType, String encoding, Set<Link> links) {
        this.statusCode = statusCode;
        this.entity = entity;
        this.cookies = cookies;
        this.headers = headers;
        this.lastModified = lastModified;
        this.allowedMethods = allowedMethods;
        this.location = location;
        this.language = language;
        this.encoding = encoding;
        this.mediaType = mediaType;
        this.links = links;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public StatusType getStatusInfo() {
        return new StatusType() {
            @Override
            public int getStatusCode() {
                return statusCode;
            }

            @Override
            public String getReasonPhrase() {
                return "Unknown";
            }

            @Override
            public Family getFamily() {
                return Status.fromStatusCode(statusCode).getFamily();
            }
        };
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasEntity() {
        return entity != null;
    }

    @Override
    public boolean bufferEntity() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return cookies;
    }

    @Override
    public EntityTag getEntityTag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public URI getLocation() {
        return location;
    }

    @Override
    public Set<Link> getLinks() {
        return links;
    }

    @Override
    public boolean hasLink(String relation) {
        return Optional.ofNullable(links)
                .flatMap(ls -> ls.stream().filter(l -> relation.equals(l.getRel())).findFirst()).isPresent();
    }

    @Override
    public Link getLink(String relation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder getLinkBuilder(String relation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return headers;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHeaderString(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class RespBuilder extends ResponseBuilder {
        int statusCode;
        Object entity;
        List<Annotation> annotations;
        Set<String> allowedMethods;
        private CacheControl cacheControl;
        private String encoding;
        private MultivaluedMap<String, Object> headers;
        private Locale language;
        private MediaType type;
        private URI location;
        private Map<String, NewCookie> cookies;
        private Date lastModified;
        private Set<Link> links;

        @Override
        public Response build() {
            return new JaxrsResponse(statusCode, entity, cookies, headers, allowedMethods, lastModified, location,
                    language, type, encoding, links);
        }

        @Override
        public ResponseBuilder clone() {
            return new RespBuilder().status(statusCode).entity(entity).allow(allowedMethods);
        }

        @Override
        public ResponseBuilder status(int status) {
            this.statusCode = status;
            return this;
        }

        @Override
        public ResponseBuilder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public ResponseBuilder entity(Object entity, Annotation[] annotations) {
            this.entity = entity;
            this.annotations = Arrays.asList(annotations);
            return this;
        }

        @Override
        public ResponseBuilder allow(String... methods) {
            return allow(new HashSet<>(Arrays.asList(methods)));
        }

        @Override
        public ResponseBuilder allow(Set<String> methods) {
            this.allowedMethods = methods;
            return this;
        }

        @Override
        public ResponseBuilder cacheControl(CacheControl cacheControl) {
            this.cacheControl = cacheControl;
            return this;
        }

        @Override
        public ResponseBuilder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        @Override
        public ResponseBuilder header(String name, Object value) {
            if (headers == null) {
                headers = new MultivaluedHashMap<>();
            }
            headers.add(name, value);
            return this;
        }

        @Override
        public ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        @Override
        public ResponseBuilder language(String language) {
            return language(Locale.forLanguageTag(language));
        }

        @Override
        public ResponseBuilder language(Locale language) {
            this.language = language;
            return this;
        }

        @Override
        public ResponseBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        @Override
        public ResponseBuilder type(String type) {
            return type(MediaType.valueOf(type));
        }

        @Override
        public ResponseBuilder variant(Variant variant) {
            Optional.ofNullable(variant.getEncoding()).ifPresent(this::encoding);
            Optional.ofNullable(variant.getLanguage()).ifPresent(this::language);
            Optional.ofNullable(variant.getMediaType()).ifPresent(this::type);
            return this;
        }

        @Override
        public ResponseBuilder contentLocation(URI location) {
            this.location = location;
            return this;
        }

        @Override
        public ResponseBuilder cookie(NewCookie... cookies) {
            if (this.cookies == null) {
                this.cookies = new HashMap<>();
            }
            Arrays.stream(cookies).forEach(cookie -> {
                this.cookies.put(cookie.getName(), cookie);
            });
            return this;
        }

        @Override
        public ResponseBuilder expires(Date expires) {
            return null;
        }

        @Override
        public ResponseBuilder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        @Override
        public ResponseBuilder location(URI location) {
            return contentLocation(location);
        }

        @Override
        public ResponseBuilder tag(EntityTag tag) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder tag(String tag) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder variants(Variant... variants) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder variants(List<Variant> variants) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ResponseBuilder links(Link... links) {
            if (this.links == null) {
                this.links = new HashSet<>();
            }
            this.links.addAll(Arrays.asList(links));
            return this;
        }

        @Override
        public ResponseBuilder link(URI uri, String rel) {
            return links(Link.fromUri(uri).rel(rel).build());
        }

        @Override
        public ResponseBuilder link(String uri, String rel) {
            return links(Link.fromUri(uri).rel(rel).build());
        }

    }
}
