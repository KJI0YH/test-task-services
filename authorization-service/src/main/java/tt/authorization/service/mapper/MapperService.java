package tt.authorization.service.mapper;

import tt.authorization.exception.MapperException;

public interface MapperService<Entity, Dto> {
    Entity dtoToEntity(Dto dto) throws MapperException;

    Dto entityToDto(Entity entity) throws MapperException;
}
